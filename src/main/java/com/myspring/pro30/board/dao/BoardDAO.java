package com.myspring.pro30.board.dao;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.myspring.pro30.board.vo.ArticleVO;


public interface BoardDAO {
	public List selectAllArticlesList(Map<String, Integer> map) throws DataAccessException;
	public int insertNewArticle(Map articleMap) throws DataAccessException;
	//public void insertNewImage(Map articleMap) throws DataAccessException;
	public ArticleVO selectArticle(int articleNO) throws DataAccessException;
	public void updateArticle(Map articleMap) throws DataAccessException;
	public void deleteArticle(int articleNO) throws DataAccessException;
	public List selectImageFileList(int articleNO) throws DataAccessException;
	public int insertReplyArticle(Map<String, Object> articleMap);
	public List<ArticleVO> selectAllNoticeArticlesList();
	public void updateArticleHits(int articleNO);
	public int selectTotArticles();
	public void updateSessionId(Map<String, Object> map);
	public String selectSessionId(int articleNO);
	void insertNewImage(Map articleMap) throws DataAccessException;
	public void updateImage(Map articleMap);
	
}
