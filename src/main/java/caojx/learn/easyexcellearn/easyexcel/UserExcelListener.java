package caojx.learn.easyexcellearn.easyexcel;

import caojx.learn.easyexcellearn.dto.UserExcelImportDto;
import caojx.learn.easyexcellearn.entity.User;
import caojx.learn.easyexcellearn.service.UserService;
import caojx.learn.easyexcellearn.util.ValidationUtils;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户导入监听器
 *
 * @author caojx created on 2023/2/21 15:05
 */
@Data
@Slf4j
public class UserExcelListener extends AnalysisEventListener<UserExcelImportDto> {

    /**
     * 每隔1000条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;

    /**
     * 缓存的数据
     */
    private List<UserExcelImportDto> cachedDataList = new ArrayList<>();

    /**
     * 失败结果集
     */
    private List<ExcelCheckResult<UserExcelImportDto>> errList = new ArrayList<>();

    private UserService userService;

    public UserExcelListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void invoke(UserExcelImportDto userExcelImportDto, AnalysisContext analysisContext) {
        Integer rowIndex = analysisContext.readRowHolder().getRowIndex();

        log.info("解析到一条数据:{} 行号={}", JSON.toJSONString(userExcelImportDto), rowIndex);

        // 数据校验
        String errMsg = ValidationUtils.validateObject(userExcelImportDto);

        if (StringUtils.isNotBlank(errMsg)) {
            // 失败结果集
            ExcelCheckResult<UserExcelImportDto> excelCheckResult = new ExcelCheckResult<>(userExcelImportDto, rowIndex, errMsg);
            errList.add(excelCheckResult);
        } else {
            // 成功结果集
            cachedDataList.add(userExcelImportDto);
        }

        // 分批保存
        if (cachedDataList.size() >= BATCH_COUNT) {
            List<User> userList = cachedDataList.stream().map(item -> {
                User user = new User();
                user.setName(item.getName());
                user.setSex(item.getSex());
                user.setAge(item.getAge());
                user.setBirthday(item.getBirthday());
                return user;
            }).collect(Collectors.toList());
            userService.batchSave(userList);

            // 存储完成清理 list
            cachedDataList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }



    /**
     * 在转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
     *
     * @param exception
     * @param analysisContext
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext analysisContext) throws Exception {
        Integer rowIndex = analysisContext.readRowHolder().getRowIndex();
        log.error("rowIndex={} 解析失败，但是继续解析下一行:{}", rowIndex, exception.getMessage());

        ExcelCheckResult excelCheckResult = new ExcelCheckResult();
        excelCheckResult.setRowIndex(rowIndex);
        excelCheckResult.setErrMsg(exception.getMessage());

        // 读取错误数据行内容
        LinkedHashMap<Integer, CellData> cellDataLinkedHashMap = (LinkedHashMap<Integer, CellData>) analysisContext.readRowHolder().getCurrentRowAnalysisResult();
        cellDataLinkedHashMap.forEach((key,value)-> System.out.println(key+"----"+value.toString()));

        errList.add(excelCheckResult);


        // 如果是某一个单元格的转换异常 能获取到具体行号
        // 如果要获取头的信息 配合invokeHeadMap使用
//        if (exception instanceof ExcelDataConvertException) {
//            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
//            log.error("第{}行，第{}列解析异常，数据为:{}", excelDataConvertException.getRowIndex(), excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData());
//        }
    }
}
