<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" 
    isELIgnored="false"  %>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />

<%
  request.setCharacterEncoding("UTF-8");
%>    


<html>
<head>
<meta charset=UTF-8">
<title>회원 정보 출력창</title>
</head>
<script>
	function fn_modMemberForm(isLogOn,modForm,loginForm, membersId){
		var loginId = "${member.id}";

		if(isLogOn != '' && isLogOn != 'false'){
		  location.href=modForm;
		}else{
		  location.href=loginForm+'?action=/member/listmembers.do';
		  alert("로그인 후 회원정보 수정이 가능합니다.")
		  return;
		}
		
		if(loginId != membersId){
			location.href="${contextPath}/member/listMembers.do";
			alert("다른 회원의 정보는 수정 불가능합니다.");
		}
	}
	
	function fn_removeMember(isLogOn,removeForm,loginForm, membersId){
		var loginId = "${member.id}";

		if(isLogOn != '' && isLogOn != 'false'){
		  location.href=removeForm;
		}else{
		  location.href=loginForm+'?action=/member/listmembers.do';
		  alert("로그인 후 회원정보 삭제가 가능합니다.")
		  return;
		}
		
		if(loginId != membersId){
			if(loginId == 'admin'){
				location.href=removeForm;
			}else{
				location.href="${contextPath}/member/listMembers.do";
				alert("다른 회원의 정보는 삭제 불가능합니다.");
			}
		}
	}
</script>
<body>
	<table border="1"  align="center"  width="80%">
	    <tr align="center"   bgcolor="lightgreen">
	      <td ><b>아이디</b></td>
	      <td><b>비밀번호</b></td>
	      <td><b>이름</b></td>
	      <td><b>이메일</b></td>
	      <td><b>가입일</b></td>
	      <td><b>수정</b></td>
	      <td><b>삭제</b></td>
	    </tr>

		<c:forEach var="members" items="${membersList}">
			<tr align="center">
				<td>${members.id}</td>
				<td>${members.pwd}</td>
				<td>${members.name}</td>
				<td>${members.email}</td>
				<td>${members.joinDate}</td>
				<td><a href="javascript:fn_modMemberForm('${isLogOn}','${contextPath}/member/modMemberForm.do',
		                                            '${contextPath}/member/loginForm.do','${members.id}')">수정하기</a></td>
				<%-- <td><a href="${contextPath}/member/modMemberForm.do?id=${member.id }">수정하기</a></td> --%>
				
				<td><a href="javascript:fn_removeMember('${isLogOn}','${contextPath}/member/removeMember.do?id=${members.id}',
		                                            '${contextPath}/member/loginForm.do','${members.id}')">삭제하기</a></td>
				<%-- <td><a href="${contextPath}/member/removeMember.do?id=${members.id}">삭제하기</a></td> --%>
			</tr>
		</c:forEach>
	</table>
	<a href="${contextPath}/member/memberForm.do"> <h1 style="text-align:center">회원가입</h1></a>
</body>
</html>
