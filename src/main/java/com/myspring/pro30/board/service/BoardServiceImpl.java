package com.myspring.pro30.board.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.myspring.pro30.board.dao.BoardDAO;
import com.myspring.pro30.board.vo.ArticleVO;
import com.myspring.pro30.board.vo.ImageVO;


@Service("boardService")
@Transactional(propagation = Propagation.REQUIRED)
public class BoardServiceImpl  implements BoardService{
	@Autowired
	BoardDAO boardDAO;
	
	public Map<String, Object> listArticles(Map<String, Integer> map) throws Exception{
		Map<String, Object> returnmap = new HashMap<String, Object>();
		
		List<ArticleVO> articlesList =  boardDAO.selectAllArticlesList(map);
		int totArticles = boardDAO.selectTotArticles();
		
		returnmap.put("articlesList", articlesList);
		returnmap.put("totArticles", totArticles);
		
        return returnmap;
	}

	
	@Override
	public int addNewArticle(Map articleMap) throws Exception{
		int articleNO = boardDAO.insertNewArticle(articleMap);
		
		List<ImageVO> imageFileList = (ArrayList)articleMap.get("imageFileList");
		
		if(imageFileList!=null && imageFileList.size() != 0 ) {
			articleMap.put("articleNO", articleNO);
			boardDAO.insertNewImage(articleMap);
		}
		
		return articleNO;
	}
	
	
	// 단일 이미지용 viewArticle
//	@Override
//	public ArticleVO viewArticle(int articleNO) throws Exception {
//		ArticleVO articleVO = boardDAO.selectArticle(articleNO);
//		return articleVO;
//	}
	
	// 다중 이미지용 viewArticle
	@Override
	public Map viewArticle(int articleNO) throws Exception {
		Map articleMap = new HashMap();
		ArticleVO articleVO = boardDAO.selectArticle(articleNO);
		List<ImageVO> imageFileList = boardDAO.selectImageFileList(articleNO);
		
		// 글 정보
		articleMap.put("article", articleVO);
		// 글 다중 이미지 List
		articleMap.put("imageFileList", imageFileList);		
		
		return articleMap;
	}
	
	
	
	@Override
	public void modArticle(Map articleMap) throws Exception {
		boardDAO.updateArticle(articleMap);
		boardDAO.updateImage(articleMap);
		System.out.println("serviceImpl updateImage 완료!!!");
	}
	
	@Override
	public void removeArticle(int articleNO) throws Exception {
		boardDAO.deleteArticle(articleNO);
	}


	@Override
	public int addReply(Map<String, Object> articleMap) {
		return boardDAO.insertReplyArticle(articleMap);
	}


	@Override
	public List listNoticeArticles() {
		List<ArticleVO> NoticearticlesList =  boardDAO.selectAllNoticeArticlesList();
        return NoticearticlesList;
	}


	@Override
	public void updateArticleHits(int articleNO) {
		boardDAO.updateArticleHits(articleNO);
	}


	@Override
	public void updateSessionId(Map<String, Object> map) {
		boardDAO.updateSessionId(map);
	}


	@Override
	public String selectSessionId(int articleNO) {
		return boardDAO.selectSessionId(articleNO);
	}
	

	
}
