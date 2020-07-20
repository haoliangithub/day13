package com.xiaoshu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Dept;
import com.xiaoshu.entity.Emp;
import com.xiaoshu.entity.EmpVo;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.DeptService;
import com.xiaoshu.service.EmpService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

@Controller
@RequestMapping("emp")
@MultipartConfig
public class EmpController extends LogController{
	static Logger logger = Logger.getLogger(EmpController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	
	@Autowired
	private EmpService empService;
	
	@Autowired
	private DeptService DeptService;
	
	@RequestMapping("empIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		List<Role> roleList = roleService.findRole(new Role());
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		List<Dept> deptList = DeptService.findAll();
		request.setAttribute("deptlist", deptList);
		return "emp";
	}
	
	
	@RequestMapping(value="empList",method=RequestMethod.POST)
	public void userList(EmpVo empVo,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			
			
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			//PageInfo<User> userList= userService.findUserPage(user,pageNum,pageSize,ordername,order);
			
			PageInfo<EmpVo> fingpage = empService.fingpage(empVo, pageNum, pageSize);
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",fingpage.getTotal() );
			jsonObj.put("rows", fingpage.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误",e);
			throw e;
		}
	}
	
	
	// 新增或修改
	@RequestMapping("reserveUser")
	public void reserveUser(MultipartFile photo,Emp emp,HttpServletRequest request,HttpServletResponse response) throws IllegalStateException, IOException{
		
		if(photo!=null){
			String path = request.getServletContext().getRealPath("/");
			
			String url = path+"/img";
			File f = new File(url);
			if(!f.exists()){
				f.mkdir();
			}
			String odlname = photo.getOriginalFilename();
			String newname = UUID.randomUUID().toString()+odlname.substring(odlname.indexOf("."));
			
			photo.transferTo(new File(url+"/"+newname));
			
			emp.setTbEmpImg("../img/"+newname);
		}
		
		
		
		
		Integer id = emp.getTbEmpId();
		JSONObject result=new JSONObject();
		try {
				Emp emp2 = empService.findByid(emp);
			if (id != null) {   // userId不为空 说明是修改
				
				if(emp2 == null || (emp2 != null && emp2.getTbEmpId().equals(id))){
						empService.update(emp);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该用人员名被使用");
				}
				
			}else {   // 添加
				if(emp2==null){  // 没有重复可以添加
					//userService.addUser(user);
					empService.addemp(emp);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用人员名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	@RequestMapping("deleteUser")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				//userService.deleteUser(Integer.parseInt(id));
				empService.delemp(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	@RequestMapping("importemp")
	public void importemp(MultipartFile newFile,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			
			empService.importEmp(newFile);

			
			result.put("success", true);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("导入人员信息错误",e);
			result.put("errorMsg", "对不起，导入失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	@RequestMapping("exportList")
	public void export(EmpVo empVo, HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			
			String time = TimeUtil.formatTime(new Date(), "yyyyMMddHHmmss");
		    String excelName = "手动备份"+time;
			List<EmpVo> list = empService.findAll(empVo);
			//List<Log> list = logService.findLog(log);
			String[] handers = {"人员编号","人员姓名","人员性别","人员年龄","人员地址","人员头像","人员生日","人员部门"};
			// 1导入硬盘
			ExportExcelToDisk(request,handers,list, excelName);
			
			
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("导出人员信息错误",e);
			result.put("errorMsg", "对不起，导出失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	// 导出到硬盘
		@SuppressWarnings("deprecation")
		private void ExportExcelToDisk(HttpServletRequest request,
				String[] handers, List<EmpVo> list, String excleName) throws Exception {
			
			try {
				HSSFWorkbook wb = new HSSFWorkbook();//创建工作簿
				HSSFSheet sheet = wb.createSheet("操作记录备份");//第一个sheet
				HSSFRow rowFirst = sheet.createRow(0);//第一个sheet第一行为标题
				rowFirst.setHeight((short) 500);
				for (int i = 0; i < handers.length; i++) {
					sheet.setColumnWidth((short) i, (short) 4000);// 设置列宽
				}
				//写标题了
				for (int i = 0; i < handers.length; i++) {
				    //获取第一行的每一个单元格
				    HSSFCell cell = rowFirst.createCell(i);
				    //往单元格里面写入值
				    cell.setCellValue(handers[i]);
				}
				for (int i = 0;i < list.size(); i++) {
				    //获取list里面存在是数据集对象
				    EmpVo vo = list.get(i);
				    //创建数据行
				    HSSFRow row = sheet.createRow(i+1);
				    //设置对应单元格的值
				    row.setHeight((short)400);   // 设置每行的高度
				    //"人员编号","人员姓名","人员性别","人员年龄","人员地址","人员头像","人员生日","人员部门"
				    row.createCell(0).setCellValue(vo.getTbEmpId());
				    row.createCell(1).setCellValue(vo.getTbEmpName());
				    row.createCell(2).setCellValue(vo.getTbEmpSex().equals("1")?"男":"女");
				    row.createCell(3).setCellValue(vo.getTbEmpAge());
				    row.createCell(4).setCellValue(vo.getTbEmpShengid()+"-"+vo.getTbEmpSid()+"-"+vo.getTbEmpXid());
				    row.createCell(5).setCellValue(vo.getTbEmpImg());
				    row.createCell(6).setCellValue(TimeUtil.formatTime(vo.getTbEmpBirthday(),"yyyy-mm-dd"));
				    row.createCell(7).setCellValue(vo.getDname());
				}
				//写出文件（path为文件路径含文件名）
					OutputStream os;
					File file = new File("E:\\"+File.separator+excleName+".xls");
					
					if (!file.exists()){//若此目录不存在，则创建之  
						file.createNewFile();  
						logger.debug("创建文件夹路径为："+ file.getPath());  
		            } 
					os = new FileOutputStream(file);
					wb.write(os);
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				}
		}
	
	
	
	@RequestMapping("editPassword")
	public void editPassword(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		HttpSession session = request.getSession();
		User currentUser = (User) session.getAttribute("currentUser");
		if(currentUser.getPassword().equals(oldpassword)){
			User user = new User();
			user.setUserid(currentUser.getUserid());
			user.setPassword(newpassword);
			try {
				userService.updateUser(user);
				currentUser.setPassword(newpassword);
				session.removeAttribute("currentUser"); 
				session.setAttribute("currentUser", currentUser);
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("修改密码错误",e);
				result.put("errorMsg", "对不起，修改密码失败");
			}
		}else{
			logger.error(currentUser.getUsername()+"修改密码时原密码输入错误！");
			result.put("errorMsg", "对不起，原密码输入错误！");
		}
		WriterUtil.write(response, result.toString());
	}
}
