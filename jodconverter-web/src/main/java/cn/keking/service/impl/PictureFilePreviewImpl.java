package cn.keking.service.impl;

import cn.keking.model.FileAttribute;
import cn.keking.model.ReturnResponse;
import cn.keking.service.FilePreview;
import cn.keking.utils.DownloadUtils;
import cn.keking.utils.FileUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Created by kl on 2018/1/17.
 * Content :图片文件处理
 */
@Service
public class PictureFilePreviewImpl implements FilePreview {

    @Autowired
    private FileUtils fileUtils;

    @Value("${file.dir}")
    private String fileDir;

    @Autowired
    private DownloadUtils downloadUtils;



    @Override
    public String filePreviewHandle(String url, Model model) {
//        String fileKey=(String) RequestContextHolder.currentRequestAttributes().getAttribute("fileKey",0);
//        List imgUrls = Lists.newArrayList(url);
//        try{
//            imgUrls.clear();
//            imgUrls.addAll(fileUtils.getRedisImgUrls(fileKey));
//        }catch (Exception e){
//            imgUrls = Lists.newArrayList(url);
//        }
        //这里使用下载工具，把需要的图片资源下载到本地，使用本地的资源进行预览
        FileAttribute fileAttribute=fileUtils.getFileAttribute(url);
        String suffix = fileAttribute.getSuffix();
//        String fileName = fileAttribute.getName();
        String decodedUrl = fileAttribute.getDecodedUrl();
        //下载下来
        //拼一个暂时暂存全路径名，检查这个文件是不是已经存在了，如果这个文件不存在，我们把这个文件下载下来
//        String filePath = fileDir + fileName;
        //需要下载回来的文件名
        String fileName = UUID.randomUUID().toString().replaceAll("-","")+System.currentTimeMillis()+ "."+suffix;
            ReturnResponse<String> response = downloadUtils.downLoad(decodedUrl, suffix, fileName);
            //文件下载成功并成功存到本地返回0
            if (0 != response.getCode()) {
                model.addAttribute("msg", response.getMsg());
                return "fileNotSupported";
            }
            //返回成功，返回内容是下载下来的文件暂存的系统路径
        String filePath = response.getContent();
            if (!new File(filePath).exists()){
                model.addAttribute("msg", "图片路径资源可能不存在！");
                return "fileNotSupported";
            }
        //我们直接用服务器前缀+文件名组成本地可以访问的路径
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        StringBuffer urlTmp = request.getRequestURL();
        String tempContextUrl = urlTmp.delete(urlTmp.length() - request.getRequestURI().length(), urlTmp.length()).toString();

        //组装成可访问的地址
        String filePathTmp=tempContextUrl+"/"+fileName;
        List imgUrls = Lists.newArrayList(filePathTmp);

        model.addAttribute("imgurls", imgUrls);
        model.addAttribute("currentUrl",filePathTmp);
        return "picture";
    }
}
