package com.xiaoshu.dao;

import java.util.List;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Content;
import com.xiaoshu.entity.ContentVo;

public interface ContentMapper extends BaseMapper<Content> {

	List<ContentVo> findAll(ContentVo contentVo);
  
}