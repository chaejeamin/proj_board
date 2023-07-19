package com.myspring.pro30.board.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.myspring.pro30.board.vo.ArticleVO;
import com.myspring.pro30.board.vo.ImageVO;


@Repository("boardDAO")
public class BoardDAOImpl implements BoardDAO {
	@Autowired
	private SqlSession sqlSession;

	@Override
	public List selectAllArticlesList(Map<String, Integer> map) throws DataAccessException {
		System.out.println("daoimpl에서 전달받은 map.section확인: " + map.get("section"));
		System.out.println("daoimpl에서 전달받은 map.pageNum확인: " + map.get("pageNum"));
		List<ArticleVO> articlesList = sqlSession.selectList("mapper.board.selectAllArticlesList", map);
		return articlesList;
	}

	
	@Override
	public int insertNewArticle(Map articleMap) throws DataAccessException {
		int articleNO = selectNewArticleNO();
		articleMap.put("articleNO", articleNO);
		
		sqlSession.insert("mapper.board.insertNewArticle",articleMap);
		
		return articleNO;
	}
	
	@Override
	public void insertNewImage(Map articleMap) throws DataAccessException {
		List<ImageVO> imageFileList = (ArrayList)articleMap.get("imageFileList");	
		int articleNO = (Integer)articleMap.get("articleNO");
		int imageFileNO = selectNewImageFileNO();
		
		for(ImageVO imageVO: imageFileList) {
			imageVO.setImageFileNO(++imageFileNO);
			imageVO.setArticleNO(articleNO);
		}
		
		sqlSession.insert("mapper.board.insertNewImage", imageFileList);
	}
    
	@Override
	public ArticleVO selectArticle(int articleNO) throws DataAccessException {
		return sqlSession.selectOne("mapper.board.selectArticle", articleNO);
	}

	@Override
	public void updateArticle(Map articleMap) throws DataAccessException {
		sqlSession.update("mapper.board.updateArticle", articleMap);
	}

	@Override
	public void deleteArticle(int articleNO) throws DataAccessException {
		sqlSession.delete("mapper.board.deleteArticle", articleNO);
		
	}
	
	@Override
	public List selectImageFileList(int articleNO) throws DataAccessException {
		List<ImageVO> imageFileList = null;
		imageFileList = sqlSession.selectList("mapper.board.selectImageFileList",articleNO);
		return imageFileList;
	}
	
	private int selectNewArticleNO() throws DataAccessException {
		return sqlSession.selectOne("mapper.board.selectNewArticleNO");
	}
	
	private int selectNewImageFileNO() throws DataAccessException {
		return sqlSession.selectOne("mapper.board.selectNewImageFileNO");
	}


	@Override
	public int insertReplyArticle(Map<String, Object> articleMap) {
		int articleNO = selectNewArticleNO();
		articleMap.put("articleNO", articleNO);
		sqlSession.insert("mapper.board.insertReplyArticle",articleMap);
		return articleNO;
	}


	@Override
	public List<ArticleVO> selectAllNoticeArticlesList() {
		List<ArticleVO> NoticearticlesList = sqlSession.selectList("mapper.board.selectAllNoticeArticlesList");
		return NoticearticlesList;
	}


	@Override
	public void updateArticleHits(int articleNO) {
		sqlSession.update("mapper.board.updateArticleHits", articleNO);
	}


	@Override
	public int selectTotArticles() {
		return sqlSession.selectOne("mapper.board.selectTotArticles");
	}


	@Override
	public void updateSessionId(Map<String, Object> map) {
		sqlSession.update("mapper.board.updateSessionId", map);
	}


	@Override
	public String selectSessionId(int articleNO) {
		return sqlSession.selectOne("mapper.board.selectSessionId", articleNO);
	}


	@Override
	public void updateImage(Map articleMap) {
		List<ImageVO> imageFileList = (ArrayList)articleMap.get("imageFileList");	
		String articleNO = (String)articleMap.get("articleNO");
		
		for(ImageVO imageVO: imageFileList) {
			imageVO.setArticleNO(Integer.parseInt(articleNO));
			// board.xml에서 foreach태그로 list를 여러번 읽을 수 없어서 여기서 imageVO를 보내서 처리
			sqlSession.update("mapper.board.updateImageFile", imageVO);
		}
		
		System.out.println("쿼리 완료");
	}


}
