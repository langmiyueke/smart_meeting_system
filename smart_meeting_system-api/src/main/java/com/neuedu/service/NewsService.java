package com.neuedu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.pojo.News;
import com.neuedu.pojo.Result;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
* @author 86186
* @description 针对表【news(新闻信息表)】的数据库操作Service
* @createDate 2025-06-17 20:53:37
*/
public interface NewsService extends IService<News> {

    Result getNews(Integer pageNum, Integer pageSize, String title, String imageUrl, String author, String summary,String order);

    Result addNews(News news);

    Result deleteNews(Long id);

    Result updateNews(News news);

    Result getTenants();

    Result batchDelete(List<Long> ids);

    void exportByIds(List<Long> idList, HttpServletResponse resp) throws IOException;

    void exportBySearch(String title, String imageUrl, String author, String summary, String order, HttpServletResponse resp) throws IOException;
}
