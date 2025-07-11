package com.neuedu.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.mapper.NewsMapper;
import com.neuedu.pojo.News;
import com.neuedu.pojo.Result;
import com.neuedu.service.NewsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author 86186
 * @description 针对表【news(新闻信息表)】的数据库操作Service实现
 * @createDate 2025-06-17 20:53:37
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News>
        implements NewsService {

    @Autowired
    NewsMapper newsMapper;

    @Override
    public Result getNews(Integer pageNum, Integer pageSize, String title, String imageUrl, String author, String summary, String orderBy) {
        Page<News> page = Page.of(pageNum, pageSize);
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<News>()
                .like(title != null && !title.isEmpty(), News::getTitle, title)
                .like(author != null && !author.isEmpty(), News::getAuthor, author)
                .like(summary != null && !summary.isEmpty(), News::getSummary, summary)
                .like(imageUrl != null && !imageUrl.isEmpty(), News::getImageUrl, imageUrl);
        // 排序字段校验并设置升序排序
        if ("title".equals(orderBy)) {
            wrapper.orderByAsc(News::getTitle);
        } else if ("author".equals(orderBy)) {
            wrapper.orderByAsc(News::getAuthor);
        } else if ("summary".equals(orderBy)) {
            wrapper.orderByAsc(News::getSummary);
        }
        return Result.success(newsMapper.selectPage(page, wrapper));
    }


    @Override
    public Result addNews(News news) {
        newsMapper.insert(news);
        return Result.success();
    }

    @Override
    public Result deleteNews(Long id) {
        newsMapper.deleteById(id);
        return Result.success();
    }

    @Override
    public Result updateNews(News news) {
        newsMapper.updateById(news);
        return Result.success();
    }

    @Override
    public Result getTenants() {
        return Result.success(newsMapper.getTenants());
    }

    // NewsServiceImpl
    @Override
    public Result batchDelete(List<Long> ids) {
        newsMapper.deleteBatchIds(ids);
        return Result.success();
    }

    @Override
    public void exportByIds(List<Long> ids, HttpServletResponse resp) throws IOException {
        List<News> list = newsMapper.selectBatchIds(ids);
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment;filename=selected_news.xlsx");

        EasyExcel.write(resp.getOutputStream(), News.class)
                .sheet("选中新闻")
                .doWrite(list);
        resp.flushBuffer();
    }

    @Override
    public void exportBySearch(String title, String imageUrl, String author, String summary, String order, HttpServletResponse resp) throws IOException {
        // 构造查询条件
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<News>()
                .like(title != null && !title.isEmpty(), News::getTitle, title)
                .like(author != null && !author.isEmpty(), News::getAuthor, author)
                .like(summary != null && !summary.isEmpty(), News::getSummary, summary)
                .like(imageUrl != null && !imageUrl.isEmpty(), News::getImageUrl, imageUrl);

        if ("title".equals(order)) wrapper.orderByAsc(News::getTitle);
        else if ("author".equals(order)) wrapper.orderByAsc(News::getAuthor);
        else if ("summary".equals(order)) wrapper.orderByAsc(News::getSummary);

        List<News> list = newsMapper.selectList(wrapper);

        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment;filename=search_news.xlsx");

        EasyExcel.write(resp.getOutputStream(), News.class)
                .sheet("搜索结果")
                .doWrite(list);
        resp.flushBuffer();
    }

}




