package com.neuedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.pojo.News;
import com.neuedu.vo.TenantVo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 86186
* @description 针对表【news(新闻信息表)】的数据库操作Mapper
* @createDate 2025-06-17 20:53:37
* @Entity com.neuedu.pojo.News
*/
public interface NewsMapper extends BaseMapper<News> {
    @Select("SELECT enterprise_mark, name FROM enterprise")
    List<TenantVo> getTenants();
}




