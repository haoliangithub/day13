package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.DeptMapper;
import com.xiaoshu.entity.Dept;
import com.xiaoshu.entity.DeptVo;

@Service
public class DeptService {

	@Autowired
	DeptMapper deptMapper;
	
	public PageInfo<Dept> fingpage(DeptVo deptVo,Integer pageNum,Integer pageSize
			,String ordername,String order){
		ordername = StringUtil.isNotEmpty(ordername)?ordername:"userid";
		order = StringUtil.isNotEmpty(order)?order:"desc";
		deptVo.setOrderByClause(ordername+" "+order);
		
		PageHelper.startPage(pageNum, pageSize);
		List<Dept> list = deptMapper.fingAll(deptVo);
		
		return new PageInfo<>(list);
	}; 

	public Dept fingByname(DeptVo deptVo){
		return deptMapper.selectOne(deptVo);
	}

	public void addDept(DeptVo deptVo) {
		// TODO Auto-generated method stub
		deptMapper.insert(deptVo);
	}

	
	public void update(DeptVo deptVAo){
		
		deptMapper.updateByPrimaryKey(deptVAo);
	}
	
	public void del(int id){
		deptMapper.deleteByPrimaryKey(id);
	}

	public List<Dept> findAll() {
		// TODO Auto-generated method stub
		return deptMapper.selectAll();
	}
	
	
	
	
	
	
}
