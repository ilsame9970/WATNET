package com.project.watnet.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.watnet.model.BoardDTO;
import com.project.watnet.model.BoardDomain;
import com.project.watnet.model.BoardEntity;
import com.project.watnet.model.PartyUserEntity;
import com.project.watnet.model.UserDomain;
import com.project.watnet.model.UserEntity;
import com.project.watnet.user.UserMapper;
import com.project.watnet.user.UserService;

@Service
public class BoardService {

	@Autowired
	private BoardMapper mapper;
	
	@Autowired
	private UserService uService;
	
	@Autowired
	private UserMapper uMapper;
	
	public int getBoardPk(PartyUserEntity p) {
		PartyUserEntity vo = mapper.selParty(p);
		if(vo==null) {
			return 0;
		}
		return vo.getBoardPk();
	}
	
	public int getLeaderPk(BoardEntity p) {
		return mapper.getLeaderPk(p);
	}
	
	//-1:이미 등록되어 있는 user		 0: insBoard 실패		그외: category return
	public int insBoard(BoardDomain p) {
		PartyUserEntity vo = new PartyUserEntity();
		vo.setUserPk(p.getUserPk());
		if(mapper.selParty(vo) != null) {
			return -1;
		}
		
		if(mapper.insBoard(p) == 1) {
			vo.setBoardPk(p.getBoardPk());
			insParty(vo);

			return p.getCategory();
		}
		return 0;
	}
	
	//party에 user 등록하기 (makeParty 할 때)
	public int insParty(PartyUserEntity p) {
		p.setIsLeader(1);
		return mapper.makeParty(p);
	}
	
	//-1:이미 가입되어있는 파티가 있음			-2: point가 부족함
	public int joinParty(PartyUserEntity p) {
		PartyUserEntity vo = new PartyUserEntity();
		vo.setUserPk(p.getUserPk());
		
		if(mapper.selParty(vo) != null) {
			return -1;
		}
		
		BoardDomain vo3 = new BoardDomain();
		vo3.setBoardPk(p.getBoardPk());
		vo3 = mapper.selBoard(vo3);
		
		uService.insPointHistory(p.getUserPk(), vo3.getPrice());
		
		UserDomain selUserVo = new UserDomain();
		selUserVo.setUserPk(p.getUserPk());
		selUserVo = uService.selUser(selUserVo);
		if(selUserVo.getUserPoint() < vo3.getPrice()) {
			return -2;
		}
		
		selUserVo.setModPoint(vo3.getPrice());
		uMapper.updPoint(selUserVo);
		
		vo.setBoardPk(p.getBoardPk());
		mapper.updPlusRecruitNum(p);
		
		return mapper.insParty(p);
	}
	
	public List<BoardEntity> selBoardList(BoardDTO p) {
		p.setsIdx(p.getRowCnt() * (p.getPage() - 1));
		return mapper.selBoardList(p);
	}
	
	public BoardDomain selBoard(BoardEntity p) {
		return mapper.selBoard(p);
	}
	
	public List<UserEntity> selUserProfile(PartyUserEntity p) {
		return mapper.selUserProfile(p);
	}
	
	public int selMaxPageNum(BoardDTO p) {
		return mapper.selMaxPageNum(p);
	}
	
	public BoardDomain selMyParty(PartyUserEntity p) {
		PartyUserEntity vo = mapper.selParty(p);
		if(vo==null) {
			BoardDomain vo2 = new BoardDomain();
			vo2.setBoardPk(0);
			return vo2;
		}
		BoardDomain vo2 = new BoardDomain();
		vo2.setBoardPk(vo.getBoardPk());
		return mapper.selBoard(vo2);
	}
	
	public int quitParty(PartyUserEntity p) {
		if(selMyParty(p) == null) {
			return 0;
		}
		PartyUserEntity vo = mapper.selParty(p);
		
		if(vo.getIsLeader() == 1) {
			mapper.quitParty(vo);
			delPost(vo);
			return mapper.delBoard(vo);
		} else {
			mapper.updMinusRecruitNum(vo);
			delPost(vo);
			return mapper.quitParty(vo);
		}
	}
	
	public int delPost(PartyUserEntity p) {
		return mapper.delPost(p);
	}
}
