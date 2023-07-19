package com.myspring.pro30.board.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.myspring.pro30.board.service.BoardService;
import com.myspring.pro30.board.vo.ArticleVO;
import com.myspring.pro30.board.vo.ImageVO;
import com.myspring.pro30.member.vo.MemberVO;

@Controller("boardController")
public class BoardControllerImpl implements BoardController {
	private static final String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	@Autowired
	BoardService boardService;
	@Autowired
	ArticleVO articleVO;

	// 게시글 전체 목록
	@Override
	@RequestMapping(value = "/board/listArticles.do", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView listArticles(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		System.out.println("viewName: " + viewName);
		
		String _section = request.getParameter("section");
		String _pageNum = request.getParameter("pageNum"); 
		System.out.println("section: " + _section + " / pageNum: " + _pageNum);
		
		int section = Integer.parseInt(((_section == null) ? "1" : _section));
		int pageNum = Integer.parseInt(((_pageNum == null) ? "1" : _pageNum));
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("section", section);
		map.put("pageNum", pageNum);
		
		List NoticearticlesList = boardService.listNoticeArticles();
		
		// 페이징
		Map<String, Object> articlesmap = new HashMap<String, Object>();
		articlesmap = boardService.listArticles(map);
		List articlesList = (List) articlesmap.get("articlesList"); //boardService.listArticles(map);
		
		System.out.println("컨트롤러로 가져온 totArticles: " + articlesmap.get("totArticles"));
		
		ModelAndView mav = new ModelAndView(viewName);
		mav.addObject("NoticearticlesList", NoticearticlesList);
		mav.addObject("articlesList", articlesList);
		mav.addObject("totArticles", articlesmap.get("totArticles"));
		mav.addObject("section", section);
		mav.addObject("pageNum", pageNum);
		return mav;
	}

	// 게시글 상세 보기
	@RequestMapping(value = "/board/viewArticle.do", method = RequestMethod.GET)
	public ModelAndView viewArticle(@RequestParam("articleNO") int articleNO, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");

		
		// 조회수 중복 방지
		HttpSession session = request.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String id = null;
		
		try {
			id = memberVO.getId();
		} catch (NullPointerException e) {
		    System.out.println("로그인 하지 않은 상태: session의 id 값은 null");
		}
		
		if(id == null) {
			id = "null"; // sql의 파라미터 값으로 null을 줄 수 없어서 그냥 문자열 null로 약속
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("articleNO", articleNO);
		
		String session_id = boardService.selectSessionId(articleNO);	
		System.out.println("---------------------가져온 session_id: " + session_id);
		
		if(session_id.equals("X")) { // 로그인 했든 안했든 조회수는 중복되지 말고 처음에만 올라야 함
			System.out.println("session_id 는 " + "X or x");
			// 게시글 조회수 +1
			boardService.updateArticleHits(articleNO);
			
			// 로그인한 id를 테이블에 update
			boardService.updateSessionId(map);
		}
		
		
		// 게시글 내용
		//articleVO = boardService.viewArticle(articleNO); // 단일 이미지용
		Map articleMap = boardService.viewArticle(articleNO);
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		//mav.addObject("article", articleVO); // 단일 이미지용
		mav.addObject("articleMap",articleMap);
		
		return mav;
	}

	// 게시글 작성
//	@Override
//	@RequestMapping(value = "/board/addNewArticle.do", method = RequestMethod.POST)
//	@ResponseBody
//	public ResponseEntity addNewArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
//			throws Exception {
//		multipartRequest.setCharacterEncoding("utf-8");
//		Map<String, Object> articleMap = new HashMap<String, Object>();
//
//		Enumeration enu = multipartRequest.getParameterNames();
//		while (enu.hasMoreElements()) {
//			String name = (String) enu.nextElement();
//			String value = multipartRequest.getParameter(name);
//			articleMap.put(name, value);
//			System.out.println("name: " + name + "/ value: " + value);
//		}
//
//		String imageFileName = upload(multipartRequest);
//		HttpSession session = multipartRequest.getSession();
//		MemberVO memberVO = (MemberVO) session.getAttribute("member");
//		String id = memberVO.getId();
//		articleMap.put("parentNO", 0);
//		articleMap.put("id", id);
//		articleMap.put("imageFileName", imageFileName);
//
//		String message;
//		ResponseEntity resEnt = null;
//		HttpHeaders responseHeaders = new HttpHeaders();
//		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
//		try {
//			int articleNO = boardService.addNewArticle(articleMap);
//			if (imageFileName != null && imageFileName.length() != 0) {
//				File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
//				File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO); // 글번호에 맞게 폴더 생성
//				FileUtils.moveFileToDirectory(srcFile, destDir, true);
//			}
//
//			message = "<script>";
//			message += " alert('글쓰기 성공');";
//			message += " location.href='" + multipartRequest.getContextPath() + "/board/listArticles.do'; ";
//			message += " </script>";
//			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
//		} catch (Exception e) {
//			File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
//			srcFile.delete();
//
//			message = " <script>";
//			message += " alert('글쓰기 실패');');";
//			message += " location.href='" + multipartRequest.getContextPath() + "/board/articleForm.do'; ";
//			message += " </script>";
//			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
//			e.printStackTrace();
//		}
//		return resEnt;
//	}
	
	// 다중이미지 게시글 쓰기 구현중
	@Override
	@RequestMapping(value = "/board/addNewArticle.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity addNewArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		Map<String, Object> articleMap = new HashMap<String, Object>();

		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			articleMap.put(name, value);
			System.out.println("name: " + name + " / value: " + value);
		}
		
		// 다중 이미지 구현중
		List<String> fileList = upload(multipartRequest);
		List<ImageVO> imageFileList = new ArrayList<ImageVO>();
		if(fileList != null && fileList.size()!=0) {
			for(String fileName: fileList) {
				ImageVO imageVO = new ImageVO();
				imageVO.setImageFileName(fileName);
				imageFileList.add(imageVO);
			}
		}
		
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String id = memberVO.getId();
		articleMap.put("parentNO", 0);
		articleMap.put("id", id);
		articleMap.put("imageFileList", imageFileList);

		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		
		String imageFileName = null;
		try {
			int articleNO = boardService.addNewArticle(articleMap);
			
			if (imageFileList != null && imageFileList.size() != 0) {
				for(ImageVO imageVO: imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO); // 글번호에 맞게 폴더 생성
					// destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
			}

			message = "<script>";
			message += " alert('글쓰기 성공');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/listArticles.do'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			if (imageFileList != null && imageFileList.size() != 0) {
				for(ImageVO imageVO: imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					srcFile.delete();
				}
			}

			message = " <script>";
			message += " alert('글쓰기 실패');');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/articleForm.do'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	// 게시글 수정
//	@Override
//	@RequestMapping(value = "/board/modArticle.do", method = RequestMethod.POST)
//	@ResponseBody
//	public ResponseEntity modArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
//			throws Exception {
//		multipartRequest.setCharacterEncoding("utf-8");
//		Map<String, Object> articleMap = new HashMap<String, Object>();
//		Enumeration enu = multipartRequest.getParameterNames();
//		while (enu.hasMoreElements()) {
//			String name = (String) enu.nextElement();
//			String value = multipartRequest.getParameter(name);
//			articleMap.put(name, value);
//		}
//
//		String imageFileName = null;//= upload(multipartRequest);
//		HttpSession session = multipartRequest.getSession();
//		MemberVO memberVO = (MemberVO) session.getAttribute("member");
//		String id = memberVO.getId();
//		articleMap.put("id", id);
//		articleMap.put("imageFileName", imageFileName);
//
//		String articleNO = (String) articleMap.get("articleNO");
//		String message;
//		ResponseEntity resEnt = null;
//		HttpHeaders responseHeaders = new HttpHeaders();
//		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
//		try {
//			boardService.modArticle(articleMap);
//			if (imageFileName != null && imageFileName.length() != 0) {
//				File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
//				File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
//				FileUtils.moveFileToDirectory(srcFile, destDir, true);
//
//				String originalFileName = (String) articleMap.get("originalFileName");
//				File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + originalFileName);
//				oldFile.delete();
//			}
//			message = "<script>";
//			message += " alert('글수정 성공');";
//			message += " location.href='" + multipartRequest.getContextPath() + "/board/viewArticle.do?articleNO="
//					+ articleNO + "';";
//			message += " </script>";
//			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
//		} catch (Exception e) {
//			File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
//			srcFile.delete();
//			message = "<script>";
//			message += " alert('글수정 실패');";
//			message += " location.href='" + multipartRequest.getContextPath() + "/board/viewArticle.do?articleNO="
//					+ articleNO + "';";
//			message += " </script>";
//			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
//		}
//		return resEnt;
//	}
	
	
	// 게시글 수정 - 멀티 이미지용
	@Override
	@RequestMapping(value = "/board/modArticle.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity modArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		Map<String, Object> articleMap = new HashMap<String, Object>();

		int count = 0;
		Enumeration<String> enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
		   String name = enu.nextElement();
		   String value = multipartRequest.getParameter(name);
		   articleMap.put(name, value);
		   System.out.println("name: " + name + " / value: " + value);

		   if (name.startsWith("newFileName")) {
		      count++;
		   }
		}
		
		// 다중 이미지 구현중
		List<String> fileList = upload(multipartRequest);
		List<ImageVO> imageFileList = new ArrayList<ImageVO>();
		if(fileList != null && fileList.size()!=0) {
			for(String fileName: fileList) {
				ImageVO imageVO = new ImageVO();
				
				imageVO.setImageFileName(fileName);
				
				String origin = (String) articleMap.get("originalFileName" + count--);
				imageVO.setOriginalFileName(origin);
				
				imageFileList.add(imageVO);
			}
		}
		
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String id = memberVO.getId();
		articleMap.put("parentNO", 0);
		articleMap.put("id", id);
		articleMap.put("imageFileList", imageFileList);
		
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		
		String imageFileName = null;
		
		String articleNO = (String) articleMap.get("articleNO");
		
		try {
			boardService.modArticle(articleMap);
			
			System.out.println("boardService.modArticle(articleMap) 완료");
			
			if (imageFileList != null && imageFileList.size() != 0) {
				for(ImageVO imageVO: imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO); // 글번호에 맞게 폴더 생성
					// destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					
					String originalFileName = imageVO.getOriginalFileName();
					File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + originalFileName);
					oldFile.delete();
				}
			}
			
			message = "<script>";
			message += " alert('글수정 성공');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/viewArticle.do?articleNO="
					+ articleNO + "';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
			srcFile.delete();
			message = "<script>";
			message += " alert('글수정 실패');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/viewArticle.do?articleNO="
					+ articleNO + "';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		}
		return resEnt;
	}
	

	// 게시글 삭제
	@Override
	@RequestMapping(value = "/board/removeArticle.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity removeArticle(@RequestParam("articleNO") int articleNO, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=UTF-8");
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			boardService.removeArticle(articleNO);
			File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
			FileUtils.deleteDirectory(destDir);

			message = "<script>";
			message += " alert('글삭제 성공');";
			message += " location.href='" + request.getContextPath() + "/board/listArticles.do';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);

		} catch (Exception e) {
			message = "<script>";
			message += " alert('글삭제 실패');";
			message += " location.href='" + request.getContextPath() + "/board/listArticles.do';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	// 답글쓰기 -  parentNO를 session에 저장 or modelandview에 담아서 return
	@Override
	@RequestMapping(value = "/board/replyForm.do", method = RequestMethod.POST)
	public ModelAndView replyForm(@RequestParam("parentNO") int parentNO, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		
		String viewName = (String) request.getAttribute("viewName");
		System.out.println("parentNO: " + parentNO + " / viewName: " + viewName);
		
		// session 방법으로 테스트
		HttpSession session = request.getSession();
		session.setAttribute("parentNO", parentNO);
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String memberId = memberVO.getId();
		session.setAttribute("memberId", memberId);
		System.out.println("memberId:" + memberId);
		
		// modelandview 방법으로 테스트
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		mav.addObject("parentNO", parentNO);
		mav.addObject("memberId", memberId);
		
		return mav;
	}
	
	// 답글쓰기
	@Override
	@RequestMapping(value = "/board/addReply.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity addReply(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		Map<String, Object> articleMap = new HashMap<String, Object>();

		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			articleMap.put(name, value);
			System.out.println("name: " + name + " / value: " + value);
		}
		
		String imageFileName = null;//= upload(multipartRequest);
		HttpSession session = multipartRequest.getSession();
		int parentNO = (int)session.getAttribute("parentNO");
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String id = memberVO.getId();
		System.out.println("parentNO: " + parentNO + "/ id: " + id + " / imageFileName: " + imageFileName);
		articleMap.put("parentNO", parentNO);
		articleMap.put("id", id);
		articleMap.put("imageFileName", imageFileName);

		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			int articleNO = boardService.addReply(articleMap);
			if (imageFileName != null && imageFileName.length() != 0) {
				File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
				File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO); // 글번호에 맞게 폴더 생성
				FileUtils.moveFileToDirectory(srcFile, destDir, true);
			}

			message = "<script>";
			message += " alert('답글쓰기 성공');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/listArticles.do'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
			srcFile.delete();

			message = " <script>";
			message += " alert('답글쓰기 실패');');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/articleForm.do'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}


	@RequestMapping(value = "/board/*Form.do", method = RequestMethod.GET)
	private ModelAndView form(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		System.out.println("viewname:: " + viewName);
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		return mav;
	}

	// 파일 업로드
//	private String upload(MultipartHttpServletRequest multipartRequest) throws Exception {
//		String imageFileName = null;
//		Map<String, String> articleMap = new HashMap<String, String>();
//		Iterator<String> fileNames = multipartRequest.getFileNames();
//
//		while (fileNames.hasNext()) {
//			String fileName = fileNames.next();
//			MultipartFile mFile = multipartRequest.getFile(fileName); // 파일 데이터 저장
//			imageFileName = mFile.getOriginalFilename();
//			File file = new File(ARTICLE_IMAGE_REPO + "\\" + fileName); // 파일이 저장될 경로와 파일 이름 설정
//			if (mFile.getSize() != 0) { // 파일의 크기가 0인지 확인
//				if (!file.exists()) { // 이미 존재 하는지 확인
//					if (file.getParentFile().mkdirs()) { // 파일이 저장될 폴더 생성
//						file.createNewFile(); // 파일 생성
//					}
//				}
//				mFile.transferTo(new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName)); // 업로드된 파일을 임시 디렉토리로 이동
//			}
//		}
//		return imageFileName; // 업로드된 파일의 원래 이름인 imageFileName 반환
//	}
	
	// 다중 이미지용 업로드
	private List<String> upload(MultipartHttpServletRequest multipartRequest) throws Exception {
		List<String> fileList= new ArrayList<String>();
		Iterator<String> fileNames = multipartRequest.getFileNames();

		while (fileNames.hasNext()) {
			String fileName = fileNames.next();
			MultipartFile mFile = multipartRequest.getFile(fileName); // 파일 데이터 저장
			String originalFilName = mFile.getOriginalFilename();
			fileList.add(originalFilName);
			File file = new File(ARTICLE_IMAGE_REPO + "\\" + fileName); // 파일이 저장될 경로와 파일 이름 설정
			if (mFile.getSize() != 0) { // 파일의 크기가 0인지 확인
				if (!file.exists()) { // 이미 존재 하는지 확인
					file.getParentFile().mkdirs(); // 파일이 저장될 폴더 생성
					mFile.transferTo(new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + originalFilName)); // 업로드된 파일을 임시 디렉토리로 이동
				}
			}
		}
		return fileList; // 업로드된 파일의 원래 이름인 imageFileName 반환
	}

}
