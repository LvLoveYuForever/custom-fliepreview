package cn.keking.service.impl;

import cn.keking.model.FileAttribute;
import cn.keking.model.ReturnResponse;
import cn.keking.service.FilePreview;
import cn.keking.utils.DownloadUtils;
import cn.keking.utils.FileUtils;
import cn.keking.utils.OfficeToPdf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Created by kl on 2018/1/17.
 * Content :处理office文件
 */
@Service
public class OfficeFilePreviewImpl implements FilePreview {

    @Autowired
    FileUtils fileUtils;

    @Value("${file.dir}")
    String fileDir;

    @Autowired
    DownloadUtils downloadUtils;

    @Autowired
    private OfficeToPdf officeToPdf;

    @Override
    public String filePreviewHandle(String url, Model model) {
        //Get some properties by parsing the url
        FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
        String suffix = fileAttribute.getSuffix();
        String fileName = fileAttribute.getName();
        String decodedUrl = fileAttribute.getDecodedUrl();
        //If the file end with .xls or .xlsx,we will convert the the file to hmtl,Else convert to pdf
        boolean isHtml = suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx");
        //把文件名改成以pdf或者hmtl结尾
//        String pdfName = fileName.substring(0, fileName.lastIndexOf(".") + 1) + (isHtml ? "html" : "pdf");

        //拼一个暂时暂存全路径名，检查这个文件是不是已经存在了，如果这个文件不存在，我们把这个文件下载下来
        String filePath = fileDir + fileName;
        if (!new File(filePath).exists()) {
            ReturnResponse<String> response = downloadUtils.downLoad(decodedUrl, suffix, null);
            //文件下载成功并成功存到本地返回0
            if (0 != response.getCode()) {
                model.addAttribute("msg", response.getMsg());
                return "fileNotSupported";
            }
            //返回成功，返回内容是下载下来的文件的路径
            filePath = response.getContent();
        }

        String pdfNameAndPath=getRandomFilePathAndName(isHtml ? "html" : "pdf");
        String outFilePath = fileDir + pdfNameAndPath;
        //如果的输出路径有字符,意味着这个路径有效
        if (StringUtils.hasText(outFilePath)) {
            //讲office文件转换成pdf格式，输出到指定的文件路径
            officeToPdf.openOfficeToPDF(filePath, outFilePath);
            //将下载下来的临时文件删掉
            File f = new File(filePath);
            if (f.exists()) {
                f.delete();
            }

            if (isHtml) {
                // 对转换后的文件进行操作(改变编码方式)  TODO 好像有点小问题，编码之后反而出现了问题,需要调试这个转码方法 mars_lv
                fileUtils.doActionConvertedFile(outFilePath);
            }
            // 加入缓存
//            fileUtils.addConvertedFile(pdfName, fileUtils.getRelativePath(outFilePath));
        }

        // 判断之前是否已转换过，如果转换过，直接返回，否则执行转换
//        if (!fileUtils.listConvertedFiles().containsKey(pdfName)) {
//            String filePath = fileDir + fileName;
//            if (!new File(filePath).exists()) {
//                ReturnResponse<String> response = downloadUtils.downLoad(decodedUrl, suffix, null);
//                if (0 != response.getCode()) {
//                    model.addAttribute("msg", response.getMsg());
//                    return "fileNotSupported";
//                }
//                filePath = response.getContent();
//            }
//            String outFilePath = fileDir + pdfName;
//            //如果的输出路径有字符
//            if (StringUtils.hasText(outFilePath)) {
//                //讲office文件转换成pdf格式，输出到指定的文件路径
//                officeToPdf.openOfficeToPDF(filePath, outFilePath);
//                File f = new File(filePath);
//                if (f.exists()) {
//                    f.delete();
//                }
//                if (isHtml) {
//                    // 对转换后的文件进行操作(改变编码方式)  TODO 好像有点小问题，编码之后反而出现了问题,需要调试这个转码方法 mars_lv
//                    fileUtils.doActionConvertedFile(outFilePath);
//                }
//                // 加入缓存
//                fileUtils.addConvertedFile(pdfName, fileUtils.getRelativePath(outFilePath));
//            }
//        }
        model.addAttribute("pdfUrl", pdfNameAndPath);
        return isHtml ? "html" : "pdf";
    }

    /**
     * 获取一个由时间和随机的文件
     * @return
     */
    private static String getRandomFilePathAndName(String suffix){
        LocalDateTime time=LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        //年月日三级文件夹
        String filePath=time.getYear()+"/"+time.getMonthValue()+"/"+time.getDayOfMonth();
        //文件名
        String fileName=System.currentTimeMillis()+UUID.randomUUID().toString().replace("-","");
        return filePath+"/"+fileName+"."+suffix;
    }

    public static void main(String[] args) {
        System.out.println(getRandomFilePathAndName("doc"));
    }
}
