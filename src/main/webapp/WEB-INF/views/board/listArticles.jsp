<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />
<%
  request.setCharacterEncoding("UTF-8");
%>  
<!DOCTYPE html>
<html>
<head>
 <style>
	.no-uline{text-decoration:none;}
	.sel-page{text-decoration:none;color:red;}
   .cls1 {text-decoration:none;}
   .cls2{text-align:center; font-size:30px;}
  </style>
  <meta charset="UTF-8">
  <title>글목록창</title>
</head>
<script>
	function fn_articleForm(isLogOn,articleForm,loginForm){
	  if(isLogOn != '' && isLogOn != 'false'){
	    location.href=articleForm;
	  }else{
	    alert("로그인 후 글쓰기가 가능합니다.")
	    location.href=loginForm+'?action=/board/articleForm.do';
	  }
	}
	
	function handleImageLoad(count) {
	    var currentDate = new Date(); // 현재 날짜
	    var writeDate = document.getElementById("writeDateCell" + count).textContent; // 게시글 작성된 날짜
	    var date = new Date(writeDate); // 게시글 작성된 날짜를 JavaScript Date 객체로 변환

	    date.setDate(date.getDate() + 1);

	    if (currentDate <= date) {
	        // 호출한 모든 태그를 count로 접근하면서 display 속성을 inline으로 변경
	        document.getElementById("newimgTag" + count).style.display = "inline";
	    }
	}
</script>

<body>
<table align="center" border="1"  width="80%"  >
  <tr height="10" align="center"  bgcolor="lightgreen">
     <td >글번호</td>
     <td >작성자</td>              
     <td >제목</td>
     <td >작성일</td>
     <td >조회수</td>
  </tr>
<c:choose>
  <c:when test="${articlesList==null }" >
    <tr  height="10">
      <td colspan="5">
         <p align="center">
            <b><span style="font-size:9pt;">등록된 글이 없습니다.</span></b>
        </p>
      </td>  
    </tr>
  </c:when>
	<c:when test="${NoticearticlesList !=null }">
		<c:forEach var="Notice_article" items="${NoticearticlesList}" varStatus="articleNum">
			<tr align="center" style="background-color: #B2FA5C;">
				<td width="5%">${articleNum.count}</td>
				<td width="10%">${Notice_article.id }</td>
				<td align='left' width="35%"><span
					style="padding-right: 30px"></span> 
					<!-- 공지글에는 답글을 달 수가 없게 해놨기 때문에 필요 없음-->
					<%-- <c:choose> --%>
						<%-- <c:when test='${Notice_article.level > 1 }'>
							<c:forEach begin="1" end="${Notice_article.level}" step="1">
								<span style="padding-left: 20px"></span>
							</c:forEach>
							<span style="font-size: 12px;">[답변]</span>
							<a class='cls1' href="javascript:fn_viewArticle('${contextPath}/board/viewArticle.do?articleNO=${Notice_article.articleNO}', '${Notice_article.articleNO}')">${Notice_article.title}</a>
							<a class='cls1'
								href="${contextPath}/board/viewArticle.do?articleNO=${Notice_article.articleNO}">${Notice_article.title}</a>
						</c:when> --%>
						<%-- <c:otherwise> --%>
							<a class='cls1'
								href="${contextPath}/board/viewArticle.do?articleNO=${Notice_article.articleNO}">${Notice_article.title }</a>
						<%-- </c:otherwise> --%>
					<%-- </c:choose> --%>
				</td>
				<td width="10%">${Notice_article.writeDate}</td>
				<td width="5%">${Notice_article.article_hits}</td>
			</tr>
		</c:forEach>
	
		<c:forEach var="article" items="${articlesList }" varStatus="articleNum">
			<tr align="center">
				<td width="5%">${articleNum.count}</td>
				<td width="10%">${article.id }</td>
				<td align='left' width="35%"><span style="padding-right: 30px"></span> 
					<c:choose>
						<c:when test='${article.level > 1 }'>
							<c:forEach begin="1" end="${article.level}" step="1">
								<span style="padding-left: 20px"></span>
							</c:forEach>
							<!-- 반복할 때마다 load되는 img 태그를 구분하기 위해 id값에 ${articleNum.count} 붙여줌 -->
							<img id="newimgTag${articleNum.count}" src="${contextPath}/resources/image/new_img.JPG" style="height:14px; display: none;" onload="handleImageLoad(${articleNum.count})">
							<img src="${contextPath}/resources/image/reply_img.JPG" style="height:15px;">
							<a class='cls1'
								href="${contextPath}/board/viewArticle.do?articleNO=${article.articleNO}">${article.title}</a>
						</c:when>
						<c:otherwise>
							<!-- 반복할 때마다 load되는 img 태그를 구분하기 위해 id값에 ${articleNum.count} 붙여줌 -->
							<img id="newimgTag${articleNum.count}" src="${contextPath}/resources/image/new_img.JPG" style="height:14px; display: none;" onload="handleImageLoad(${articleNum.count})">
							<a class='cls1'
								href="${contextPath}/board/viewArticle.do?articleNO=${article.articleNO}">${article.title}</a>
						</c:otherwise>
					</c:choose>
				</td>
				<td id="writeDateCell${articleNum.count}" width="10%">${article.writeDate}</td>
				<td width="5%">${article.article_hits}</td>
			</tr>
		</c:forEach>
	</c:when>
		</c:choose>
</table>
<!-- <a  class="cls1"  href="#"><p class="cls2">글쓰기</p></a> -->
<div class="txt_center">
	<c:if test="${totArticles != null}">
    	<c:set var="totalPage" value="${(totArticles/10) + 1}" />
    	<c:set var="startPage" value="${(section-1)*10 + 1}" />
   		<c:set var="endPage" value="${(startPage + 9)}" />
    	
    	<c:if test="${section > 1}">
            <a class="no-uline" href="${contextPath}/board/listArticles.do?section=${section - 1}&pageNum=10">pre</a>
        </c:if>
        
        <c:forEach var="page" begin="${startPage}" end="${endPage}" step="1">
        	<c:if test="${page <= totalPage}">
                <a class="no-uline" href="${contextPath}/board/listArticles.do?section=${section}&pageNum=${page-((section-1)*10)}">
                    ${page}
                </a>
                <c:set var="lastPage" value="${page}" />
        	</c:if>
        </c:forEach>
    	
    	<c:if test="${(lastPage%10)==0}">
               <a class="no-uline" href="${contextPath}/board/listArticles.do?section=${section + 1}&pageNum=1">next</a>
        </c:if>
	</c:if>
	</div>
<a  class="cls1"  href="javascript:fn_articleForm('${isLogOn}','${contextPath}/board/articleForm.do', 
                                                    '${contextPath}/member/loginForm.do')"><p class="cls2">글쓰기</p></a>
</body>
</html>