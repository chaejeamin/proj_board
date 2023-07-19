<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />

<c:set var="article"  value="${articleMap.article}"  />
<c:set var="imageFileList"  value="${articleMap.imageFileList}"  />


<%
  request.setCharacterEncoding("UTF-8");
%> 

<head>
   <meta charset="UTF-8">
   <title>글보기</title>
   <style>
     #tr_file_upload{
       display:none;
     }
     #tr_btn_modify{
       display:none;
     }
   
   </style>
   <script  src="http://code.jquery.com/jquery-latest.min.js"></script> 
   <script type="text/javascript" >
     function backToList(obj){
	    obj.action="${contextPath}/board/listArticles.do";
	    obj.submit();
     }
 
	 function fn_enable(obj, count){
		 document.getElementById("i_title").disabled=false;
		 document.getElementById("i_content").disabled=false;
		 
		 /* document.getElementById("i_imageFileName").disabled=false; */
		 
		 var elements = document.querySelectorAll('[id^="i_imageFileName"]');
		 // 선택된 엘리먼트들을 반복하여 원하는 동작 수행
		 for (var i = 0; i < elements.length; i++) {
		   elements[i].disabled = false;
		 }
		 
		 document.getElementById("tr_btn_modify").style.display="block";
		 document.getElementById("tr_file_upload").style.display="block";
		 document.getElementById("tr_btn").style.display="none";
	 }
	 
	 function fn_modify_article(obj){
		 obj.action="${contextPath}/board/modArticle.do";
		 obj.submit();
	 }
	 
	 function fn_remove_article(url,articleNO){
		 var form = document.createElement("form");
		 form.setAttribute("method", "post");
		 form.setAttribute("action", url);
		 
	     var articleNOInput = document.createElement("input");
	     articleNOInput.setAttribute("type","hidden");
	     articleNOInput.setAttribute("name","articleNO");
	     articleNOInput.setAttribute("value", articleNO);
		 
	     form.appendChild(articleNOInput); // parentNOInput 입력 필드를 form 폼 요소의 자식 요소로 추가
	     document.body.appendChild(form); // form 폼 요소를 문서의 <body> 요소의 자식 요소로 추가
	     form.submit(); // 폼이 서버에 데이터를 전송하고, 서버의 응답을 받는 과정을 시작
	 }
	 
	 function fn_reply_form(isLogOn, url, parentNO, loginForm){
		 if(isLogOn != '' && isLogOn != 'false'){
			 var form = document.createElement("form");
			 form.setAttribute("method", "post");
			 form.setAttribute("action", url);
			 
		     var parentNOInput = document.createElement("input");
		     parentNOInput.setAttribute("type","hidden");
		     parentNOInput.setAttribute("name","parentNO");
		     parentNOInput.setAttribute("value", parentNO);
			 
		     form.appendChild(parentNOInput); // parentNOInput 입력 필드를 form 폼 요소의 자식 요소로 추가
		     document.body.appendChild(form); // form 폼 요소를 문서의 <body> 요소의 자식 요소로 추가
			 form.submit();
		  }else{
		    alert("로그인 후 글쓰기가 가능합니다.");
		    location.href=loginForm+'?action=/board/replyForm.do?parentNO='+parentNO;
		  }
	 }
	 	 
	 function readURL(input, count) {
	     if (input.files && input.files[0]) {
	         var reader = new FileReader();
	         reader.onload = function (e) {
	             $('#preview'+count).attr('src', e.target.result);
	             
	             var filename = input.files[0].name;
	             $('input[name="newFileName'+count+'"]').val(filename);
	         }
	         reader.readAsDataURL(input.files[0]);
	         
	     }
	 }  
	 
 </script>
</head>
<body>
  <form name="frmArticle" method="post"  action="${contextPath}"  enctype="multipart/form-data">
  <table  border=0  align="center">
  <tr>
   <td width=150 align="center" bgcolor=#FF9933>
      글번호
   </td>
   <td >
    <input type="text"  value="${article.articleNO }"  disabled />
    <input type="hidden" name="articleNO" value="${article.articleNO}"  />
   </td>
  </tr>
  <tr>
    <td width="150" align="center" bgcolor="#FF9933">
      작성자 아이디
   </td>
   <td >
    <input type=text value="${article.id }" name="writer"  disabled />
   </td>
  </tr>
  <tr>
    <td width="150" align="center" bgcolor="#FF9933">
      제목 
   </td>
   <td>
    <input type=text value="${article.title }"  name="title"  id="i_title" disabled />
   </td>   
  </tr>
  <tr>
    <td width="150" align="center" bgcolor="#FF9933">내용</td>
   	<td>
    	<textarea rows="20" cols="60"  name="content"  id="i_content"  disabled />${article.content }</textarea>
   	</td>  
  </tr>
  <tr>
    <td width="150" align="center" bgcolor="#FF9933">조회수</td>
   	<td>
    	<textarea rows="1" cols="60"  name="content"  id="i_hits"  disabled />${article.article_hits }</textarea>
   	</td>  
  </tr>
 
 <!-- 다중 이미지 -->
 <c:if test="${not empty imageFileList && imageFileList!='null' }">
	  <c:forEach var="item" items="${imageFileList}" varStatus="status" >
		    <tr>
			   <td width="150" align="center" bgcolor="#FF9933"  rowspan="2">
			      	이미지${status.count}
			   </td>
			   <td>
			     <input type= "hidden" name="originalFileName${status.count}" value="${item.imageFileName}"/>
			     <input type= "hidden" name="newFileName${status.count}" value=""/>
			     <img src="${contextPath}/download.do?articleNO=${article.articleNO}&imageFileName=${item.imageFileName}" id="preview${status.count}"  /><br>
			   </td>   
			</tr>  
			<tr>
			   <td>
			       <input type="file" name="imageFileName${status.count}" id="i_imageFileName${status.count}" disabled onchange="readURL(this,${status.count});"   />
			   </td>
			</tr>
		</c:forEach>
 </c:if>
 	    
 	 <!-- 단일 이미지용 -->
  <%-- <c:choose> 
	  <c:when test="${not empty article.imageFileName && article.imageFileName!='null' }">
	   	<tr>
		    <td width="150" align="center" bgcolor="#FF9933"  rowspan="2">
		     	 이미지
		    </td>
		    <td>
		      <input  type= "hidden"   name="originalFileName" value="${article.imageFileName }" />
		      <img src="${contextPath}/download.do?articleNO=${article.articleNO}&imageFileName=${article.imageFileName}" id="preview"  /><br>
		    </td>   
	    </tr>  
		<tr>
		    <td ></td>
		    <td>
		       <input  type="file"  name="imageFileName " id="i_imageFileName"   disabled   onchange="readURL(this);"   />
		    </td>
		</tr> 
	  </c:when>
		 <c:otherwise>
		    <tr id="tr_file_upload" >
			    <td width="150" align="center" bgcolor="#FF9933"  rowspan="2">
			      	이미지
			    </td>
			    <td>
			      <input  type= "hidden"   name="originalFileName" value="${article.imageFileName }" />
			    </td>
			</tr>
			<tr>
				<td ></td>
			    <td>
			       <img id="preview"  /><br>
			       <input  type="file"  name="imageFileName " id="i_imageFileName"   disabled   onchange="readURL(this);"   />
			    </td>
			</tr>
		 </c:otherwise>
	 </c:choose> --%>
	 
  <tr>
	   <td width="150" align="center" bgcolor="#FF9933">
	      등록일자
	   </td>
	   <td>
	    <input type=text value="<fmt:formatDate value="${article.writeDate}" />" disabled />
	   </td>   
  </tr>
  <tr   id="tr_btn_modify"  align="center"  >
	   <td colspan="2"   >
	       <input type=button value="수정반영하기"   onClick="fn_modify_article(frmArticle)"  >
           <input type=button value="취소"  onClick="backToList(frmArticle)">
	   </td>   
  </tr>
    
  <tr  id="tr_btn"    >
   <td colspan="2" align="center">
       <c:if test="${member.id == article.id }">
	      <input type=button value="수정하기" onClick="fn_enable(this.form, ${status.count})">
	      <input type=button value="삭제하기" onClick="fn_remove_article('${contextPath}/board/removeArticle.do', ${article.articleNO})">
	    </c:if>
	    <input type=button value="리스트로 돌아가기"  onClick="backToList(this.form)">
	    <script>
		  var articleDiv = "${article.article_div}";
		  if (articleDiv !== 'N') {
		    document.write('<input type="button" value="답글쓰기" onClick="fn_reply_form(\'${isLogOn}\',\'${contextPath}/board/replyForm.do?articleNO=${article.articleNO}\', ${article.articleNO}, \'${contextPath}/member/loginForm.do\')">');
		  }
		</script>
   </td>
  </tr>
 </table>
 </form>
</body>
</html>