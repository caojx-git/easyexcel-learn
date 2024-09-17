package caojx.learn.easyexcellearn.controller;

import caojx.learn.easyexcellearn.entity.TestEntity;
import caojx.learn.easyexcellearn.entity.TestVo;
import caojx.learn.easyexcellearn.service.ExcelService;
import caojx.learn.easyexcellearn.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Function;

/**
 * @author trg
 * @title: TestController
 * @projectName df-platform
 * @description: TODO
 * @date 2023/6/1915:22
 */
@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class TestController {

    private final ExcelService excelService;

    private final TestService testService;


    @PostMapping("/importExcel")
    public void importExcel(@RequestParam MultipartFile file) {
        // 方式1
        excelService.importExcel(file, TestEntity.class, 2, testService::saveBatch);


        // 方式2 带转换
        Function<TestEntity, TestVo> map = new Function<TestEntity, TestVo>() {
            @Override
            public TestVo apply(TestEntity testEntities) {
                TestVo testVo = new TestVo();
                testVo.setNum(testEntities.getNum());
                testVo.setSex(testEntities.getSex());
                testVo.setBaseName(testEntities.getName());
                return testVo;
            }
        };
        excelService.importExcel(file, TestEntity.class, 2, map, testService::saveBatchTest);
    }

    @PostMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        String filePath = "";
        excelService.exportExcel(testService.list(), TestEntity.class, filePath, response);
    }

}