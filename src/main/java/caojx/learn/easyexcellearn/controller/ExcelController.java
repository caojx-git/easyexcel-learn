package caojx.learn.easyexcellearn.controller;

import caojx.learn.easyexcellearn.dto.UserExcelExportDto;
import caojx.learn.easyexcellearn.dto.UserExcelImportDto;
import caojx.learn.easyexcellearn.dto.UserExcelImportErrDto;
import caojx.learn.easyexcellearn.easyexcel.UserExcelListener;
import caojx.learn.easyexcellearn.entity.User;
import caojx.learn.easyexcellearn.service.UserService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * excel controller
 *
 * @author caojx created on 2023/2/21 14:32
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Resource
    private UserService userService;

    @Resource
    private ResourceLoader resourceLoader;

    /**
     * 导出excel
     */
    @GetMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        List<User> userList = userService.getAllUser();

        List<UserExcelExportDto> userExcelExportDtoList = userList.stream().map(user -> {
            UserExcelExportDto userExcelExportDto = new UserExcelExportDto();
            userExcelExportDto.setName(user.getName());
            userExcelExportDto.setSex(user.getSex());
            userExcelExportDto.setAge(user.getAge());
            userExcelExportDto.setBirthday(user.getBirthday());
            return userExcelExportDto;
        }).collect(Collectors.toList());

        // 返回文件
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("测试用户", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 写文件
        EasyExcel.write(response.getOutputStream(), UserExcelExportDto.class).sheet().doWrite(userExcelExportDtoList);
    }

    /**
     * 导入excel
     *
     * @param response
     * @param file
     */
    @PostMapping("/importExcel")
    public void importExcel(HttpServletResponse response, @RequestParam MultipartFile file) throws IOException {

        UserExcelListener userExcelListener = new UserExcelListener(userService);
        // headRowNumber excel中头的行数，默认1行
        EasyExcel.read(file.getInputStream(), UserExcelImportDto.class, userExcelListener).sheet().headRowNumber(1).doRead();

        if (CollectionUtils.isNotEmpty(userExcelListener.getErrList())) {

            // 错误列集合
            List<UserExcelImportErrDto> userExcelImportErrList = userExcelListener.getErrList().stream().map(excelCheckError -> {
                UserExcelImportErrDto userExcelImportErrDto = JSON.parseObject(JSON.toJSONString(excelCheckError.getT()), UserExcelImportErrDto.class);
                userExcelImportErrDto.setErrMsg(excelCheckError.getErrMsg());
                return userExcelImportErrDto;
            }).collect(Collectors.toList());

            // 返回文件
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("用户导入错误信息", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 仅将错误数据写文件
            EasyExcel.write(response.getOutputStream(), UserExcelImportErrDto.class).sheet().doWrite(userExcelImportErrList);
        }
    }


    /**
     * 导入excel，追加错误信息到原始文件中
     *
     * @param response
     * @param file
     */
    @PostMapping("/importExcel2")
    public void importExcel2(HttpServletResponse response, @RequestParam MultipartFile file) throws IOException {

        UserExcelListener userExcelListener = new UserExcelListener(userService);
        EasyExcel.read(file.getInputStream(), UserExcelImportDto.class, userExcelListener).sheet().doRead();

        if (CollectionUtils.isNotEmpty(userExcelListener.getErrList())) {

            // 返回文件
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("用户导入错误信息", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 在原文件的基础上追加错误列

            // 写文件
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), UserExcelImportErrDto.class).withTemplate(file.getInputStream()).build();

            Sheet sheet = excelWriter.writeContext().writeWorkbookHolder().getCachedWorkbook().getSheetAt(0);

            // 追加错误列头
            Row headRow = sheet.getRow(0);
            Cell cell = headRow.createCell(headRow.getLastCellNum());
            cell.setCellStyle(headRow.getCell(0).getCellStyle());
            cell.setCellValue("错误原因");

            // 错误原因
            userExcelListener.getErrList().forEach(excelCheckError -> {
                Row row = sheet.getRow(excelCheckError.getRowIndex());
                row.createCell(row.getLastCellNum()).setCellValue(excelCheckError.getErrMsg());
            });
            excelWriter.finish();
        }
    }


    /**
     * 可以基于空白模版来构造数据
     *
     * @param response
     * @param file
     */
    @PostMapping("/importExcel3")
    public void importExcel3(HttpServletResponse response, @RequestParam MultipartFile file) throws IOException {

        UserExcelListener userExcelListener = new UserExcelListener(userService);
        EasyExcel.read(file.getInputStream(), UserExcelImportDto.class, userExcelListener).sheet().doRead();

        if (CollectionUtils.isNotEmpty(userExcelListener.getErrList())) {

            // 返回文件
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("用户导入错误信息", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 写文件
//            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), UserExcelImportErrDto.class).build();

            // 可以基于空白模版来构造数据
            File templateErrorResultFile = resourceLoader.getResource("classpath:template-error-result.xlsx").getFile();
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), UserExcelImportErrDto.class).needHead(false).withTemplate(templateErrorResultFile).build();

            // 错误列集合
            List<UserExcelImportErrDto> userExcelImportErrList = userExcelListener.getErrList().stream().map(excelCheckError -> {
                UserExcelImportErrDto userExcelImportErrDto = JSON.parseObject(JSON.toJSONString(excelCheckError.getT()), UserExcelImportErrDto.class);
                userExcelImportErrDto.setErrMsg(excelCheckError.getErrMsg());
                return userExcelImportErrDto;
            }).collect(Collectors.toList());

            WriteSheet writerSheet = EasyExcel.writerSheet(0).build();
            excelWriter.write(userExcelImportErrList, writerSheet);
            excelWriter.finish();
        }
    }
}
