package com.xiaoshu.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.DeptMapper;
import com.xiaoshu.dao.EmpMapper;
import com.xiaoshu.entity.Dept;
import com.xiaoshu.entity.Emp;
import com.xiaoshu.entity.EmpVo;

@Service
public class EmpService {

	@Autowired
	private EmpMapper empMapper;
	
	@Autowired
	private DeptMapper deptMapper;

	public PageInfo<EmpVo> fingpage(EmpVo empVo,Integer pageNum,Integer pageSize){
		PageHelper.startPage(pageNum, pageSize);
		List<EmpVo> list = empMapper.findAll(empVo);
		
		return new PageInfo<>(list);
	}
	
	public Emp findByid(Emp emp){
		return  empMapper.selectOne(emp);
	}

	public void update(Emp emp){
		empMapper.updateByPrimaryKeySelective(emp);
	}
	
	public void addemp(Emp emp){
		empMapper.insert(emp);
	}
	
	public void delemp(Integer id){
		empMapper.deleteByPrimaryKey(id);
	}

	public List<EmpVo> findAll(EmpVo empVo) {
		// TODO Auto-generated method stub
		return empMapper.findAll(empVo);
	}

	public void importEmp(MultipartFile newFile) throws InvalidFormatException, IOException{
		
		@SuppressWarnings("static-access")
		Workbook wd = new WorkbookFactory().create(newFile.getInputStream());
		
		Sheet sheet = wd.getSheetAt(0);
		int rowNum = sheet.getLastRowNum();
		for (int i = 0; i <rowNum; i++) {
			Row row = sheet.getRow(i+1);
			
			String name = row.getCell(0).toString();//姓名
			String sex = row.getCell(1).toString();//性别
			long age = (long) row.getCell(2).getNumericCellValue();//年龄
			//row.getCell(3).toString();//地址
			Date date = row.getCell(3).getDateCellValue();//生日
			String img = row.getCell(4).toString();//头像
			String dname = row.getCell(5).toString();//部门
			
			EmpVo emp = new EmpVo();
			emp.setTbEmpName(name);
			emp.setTbEmpSex(sex.equals("1")?"男":"女");
			emp.setTbEmpAge((int)age);
			emp.setTbEmpBirthday(date);
			emp.setTbEmpImg(img);
			Dept dept = findByname(dname);
			emp.setTbEmpDid(dept.getId());
			
			empMapper.insert(emp);
			
		
		}
	}
	
	public Dept findByname(String name){
		
		Dept dept = new Dept();
		dept.setName(name);
		return deptMapper.selectOne(dept);
	}
}
