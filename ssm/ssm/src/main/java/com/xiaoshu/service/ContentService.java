package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.ContentMapper;
import com.xiaoshu.entity.Content;
import com.xiaoshu.entity.ContentVo;
@Service
public class ContentService {

	@Autowired
	private ContentMapper contentMapper;
	
	
	
	public PageInfo<ContentVo> findPage(ContentVo contentVo,Integer pageNum,Integer pageSize){
		PageHelper.startPage(pageNum, pageSize);
		List<ContentVo> clist = contentMapper.findAll(contentVo);
		
		return new PageInfo<>(clist);
	}
	
	public void del(int id){
		contentMapper.deleteByPrimaryKey(id);
	}
	public Content selname(String name){
		Content content = new Content();
		content.setContenttitle(name);
		return contentMapper.selectOne(content);
	}

	public void add(Content content) {
		// TODO Auto-generated method stub
		contentMapper.insert(content);
	}
	
	public void updateByKey(Content content){
		contentMapper.updateByPrimaryKeySelective(content);
	}
}
