package cn.keking.service;

import cn.keking.model.FileAttribute;
import cn.keking.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Map;

/**
 * Created by kl on 2018/1/17.
 * Content :
 */
@Service
public class FilePreviewFactory {

    @Autowired
    FileUtils fileUtils;

    @Autowired
    ApplicationContext context;

    public FilePreview get(String url) {
        //获取所有的FilePreview.class所有的bean实现
        Map<String, FilePreview> filePreviewMap = context.getBeansOfType(FilePreview.class);
        //根据请求url解析出文件的属性
        FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
        //返回文件格式对应的预览处理bean
        return filePreviewMap.get(fileAttribute.getType().getInstanceName());
    }
}
