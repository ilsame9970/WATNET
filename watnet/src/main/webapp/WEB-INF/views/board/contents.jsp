<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<input type="hidden" id="currentPage" value="2">
<button id="goUp" type="button" onclick="up()"></button>

<div class="contents-container">
	<span class="contents-info">��ư�� Ŭ���Ͻø� �ش� ����Ʈ�� Contents�� ��µ˴ϴ�.<br>�� Content�� Ŭ���Ͻø� ���û���Ʈ���� ������ Ȯ���Ͻ� �� �ֽ��ϴ�.</span>
	<div class="contentBtn-container">
		<input type="button" id="netflixBtn" value="NETFLIX">
		<input type="button" id="watchaBtn" value="WATCHA">
	</div>
	<div id="conList"></div>
</div>