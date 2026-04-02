package com.mall.job;


import com.mall.service.OrderService;
import com.mall.es.ProductSyncService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderCloseJob implements Job {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductSyncService productSyncService;

    public void execute(JobExecutionContext context){
        orderService.closeTimeoutOrders();
//        productSyncService.syncAll();
    }
}