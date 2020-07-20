package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.CompanyMapper;
import com.xiaoshu.dao.PersonMapper;
import com.xiaoshu.entity.Company;
import com.xiaoshu.entity.Person;
import com.xiaoshu.entity.PersonVo;

@Service
public class PerService {


	@Autowired
	private PersonMapper personMapper;
	@Autowired
	private CompanyMapper companyMapper;
	
	public PageInfo<PersonVo> findPage(PersonVo per,Integer pageNum,Integer pageSize){
		PageHelper.startPage(pageNum, pageSize);
		
		List<PersonVo> list = personMapper.findAll(per);
		
		return new PageInfo<>(list);
		
	}
	
	public List<Company> findAll(){
		return companyMapper.selectAll();
	}
	
	public Person find(Person per){
		return  personMapper.selectOne(per);
	}
	
	public void del(int id){
		personMapper.deleteByPrimaryKey(id);
	}
	public void update(Person per){
		personMapper.updateByPrimaryKeySelective(per);
	}
	
	public void add(Person per){
		personMapper.insert(per);
	}
	
	
	
	


	


}
