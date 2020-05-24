package com.ruoyi.project.kwswitch.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.project.kwswitch.document.KwSwitchLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class KwSwitchLogServiceImpl {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 查询所有
     *
     * @rturn
     */
    public List<KwSwitchLog> findAll() {
        return mongoTemplate.findAll(KwSwitchLog.class);
    }

    /**
     * 根据设备ID查询数据
     *
     * @param deviceId
     * @return
     */
    public List<KwSwitchLog> getKwSwitchLogByDeviceId(Long deviceId) {
        Query query = new Query(Criteria.where("deviceId").is(deviceId)).limit(100);
        return mongoTemplate.find(query.with(Sort.by(Sort.Order.desc("createTime"))), KwSwitchLog.class);
    }

    /**
     * 根据ID查询数据
     *
     * @param id
     * @return
     */
    public KwSwitchLog getKwSwitchLogById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, KwSwitchLog.class);
    }

    /**
     * 保存对象
     *
     * @param kwSwitchLog
     * @return
     */
    public String saveObj(KwSwitchLog kwSwitchLog) {
        kwSwitchLog.setCreateTime(DateUtils.getNowDate());
        mongoTemplate.save(kwSwitchLog);
        return "save success";
    }

    /**
     * 更新对象
     *
     * @param kwSwitchLog
     * @return
     */
    public String updateKwSwitchLog(KwSwitchLog kwSwitchLog) {
        Query query = new Query(Criteria.where("_id").is(kwSwitchLog.getId()));
        Update update = new Update()
                .set("deviceId", kwSwitchLog.getDeviceId())
                .set("switchA", kwSwitchLog.getSwitchA())
                .set("switchB", kwSwitchLog.getSwitchB())
                .set("humidity", kwSwitchLog.getHumidity())
                .set("temperature", kwSwitchLog.getTemperature());
        // 更新第一条并返回第一条
        mongoTemplate.updateFirst(query, update, KwSwitchLog.class);
        //更新全部并返回全部
        // mongoTemplate.updateMulti(query,update,KwSwitchLog.class);
        // 更新对像，不存在，则不添加
        // mongoTemplate.upsert(query, update, KwSwitchLog.class);
        return "success-update";
    }

    /**
     * 删除对象
     *
     * @param kwSwitchLog
     * @return
     */
    public String deleteKwSwitchLog(KwSwitchLog kwSwitchLog) {
        mongoTemplate.remove(kwSwitchLog);
        return "success";
    }

    /**
     * 根据ID删除对象
     *
     * @param id
     * @return
     */
    public String deleteKwSwitchLogById(String id) {
        KwSwitchLog kwSwitchLog = getKwSwitchLogById(id);
        deleteKwSwitchLog(kwSwitchLog);
        return "success-delete";
    }

//    //模糊查询
//    public List<Book> findByLikes(String search)
//    {
//        Query query = new Query();
//        Criteria criteria = new Criteria();
//        Pattern pattern = Pattern.compile("^.*"+search+".*$",Pattern.CASE_INSENSITIVE);
//        criteria.where("name").regex(pattern);
//        List<Book> lists = mongoTemplate.findAllAndRemove(query, Book.class);
//        return lists;
//    }

}
