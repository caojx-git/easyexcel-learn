# 封装easy excel 导入导出实现思路

封装easy excel实现对大文件的excel 导入导出，思路（这里只提供了一个思路，还没有实现）

1.统一的导入导出入口，参考 TestController，可以避免导入导出入口散乱  
2.对于导入excel场景
 - 提供默认ExcelListener用于读取数据
 - 读取到的数据入库处理，<T, R> void importExcel(MultipartFile file, Class<T> tClass, Integer headRowNumber, Function<T, R> map, Consumer<List<R>> consumer); 类似该方法，提供Consumer，不同的业务实现不同的consumer
 - 如果导入场景很多，可以通过枚举映射，根据场景绑定读取数据的ExcelListener、和处理数据的Consumer，就可以通过统一的导入入口适配不同的场景
 - 对于导入数据量大，为了避免OOM出现，可以分批读取excel数据，可以参考 UserExcelListener 做法
 - 如果需要校验数据结果，参考UserExcelListener、ExcelController
 - 如果需要带参数，可以提供ImportBaseParam基础类，其他的类继承该类即可

3.对于导出excel的场景
 - 可以提供Supplier，用于提供数据查询逻辑实现 具体的查询填充excel逻辑，结合使用 ExcelGenerator实现数据填充，支持指定sheet、分页查询填充
 - 如果需要带参数，可以提供ExportBaseParam基础类，其他的类继承该类即可
 - 如果导出的场景很多，可以提供枚举映射，根据类型映射到Supplier，就可以通过统一的导出入口适配不同的业务场景
```java
@Override
    public void downloadFirstDistributorPayExecProgress(PayExecProgressQueryParam param, HttpServletResponse response) throws Exception {
        ChannelTypeEnum channelTypeEnum = ChannelTypeEnum.valueOf(param.getChannelTypeCode());
        String fileName = String.format("%s%s订单系统兑付进度数据.xlsx", param.getPeriod(), channelTypeEnum.getDesc());

        param.setPageNum(1L);
        param.setPageSize((long) configs.getBatchSize());
        fillAuthorizationContext(param);

        try (ExcelGenerator generator = new ExcelGenerator(fileName, response)) {
            Class<? extends BasePayExecProgressFirstDistributorCollectDTO> payExecProgressFirstDistributorCollectDTOClazz = getPayExecProgressFirstDistributorCollectDTOClazz(param);
            Class<? extends BasePayExecProgressFirstDistributorDetailDTO> payExecProgressFirstDistributorDetailDTOClazz = getPayExecProgressFirstDistributorDetailDTOClazz(param);

            generator.generateSheet(0, "订单系统-支付商业兑付进度汇总", payExecProgressFirstDistributorCollectDTOClazz);
            generator.generateSheet(1, "订单系统-支付商业已上传兑付进度明细", payExecProgressFirstDistributorDetailDTOClazz);
            CompletableFuture<Void> allOf = CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> generator.pageGenerateData(0, param, this::queryPayExecProgressFirstDistributorCollect), payQueryThreadPoolExecutor),
                    CompletableFuture.runAsync(() -> generator.pageGenerateData(1, param, this::queryPayExecProgressFirstDistributorDetail), payQueryThreadPoolExecutor)
            );
            allOf.join();
        }
    }
```

4.如果需要异步导入导出
- 导入时将导入文件上传到OSS，保存一个数据解析任务入库，然后将导入解析任务发送MQ消息，MQ异步解析文件，界面中提供统一的入口查看导入进度即可
- 导出时发送导出MQ消息，然后异步消费消息处理导出任务，生成文件后将文件上传到OSS入库，到后界面中统一的入口下载导出文件即可


