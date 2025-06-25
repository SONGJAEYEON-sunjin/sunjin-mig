package com.kcube.trns.sunjin.migration.doc;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.util.StringUtils;

import java.util.Set;

@Slf4j
public class DocHtmlTrnsUtil {

    private static final String EDIT_INLINT_CSS = """
            BODY  { font-family: gulim; background-color:#FFFFFF; font-size:10pt; color: #000000; margin:0px; padding:0px; line-height: 140%; scrollbar-face-color: #F2F2F2; scrollbar-shadow-color:#999999; scrollbar-highlight-color:#999999;	scrollbar-3dlight-color: #FFFFFF; scrollbar-darkshadow-color: #FFFFFF; scrollbar-track-color: #F6F6F6; scrollbar-arrow-color: #999999; }\s
            TABLE { font-family: gulim; font-size:9pt; }
            INPUT { font-size:9pt; border: solid 1px #ffffff;}
                        
            .ZD		{ font-size:8pt; color:Gray;  cursor:pointer; padding:2px; vertical-align:bottom; display:inline-block; }
            .ZP		{ font-size:8pt; color:Gray;  cursor:pointer; padding:2px; vertical-align:bottom; display:inline-block; }
            .ZQ		{ font-size:8pt; color:Gray;  cursor:pointer; padding:2px; vertical-align:bottom; display:inline-block; }
            .ZH		{ font-size:8pt; color:Gray;  cursor:pointer; padding:2px; vertical-align:bottom; display:inline-block; }
            .ZC		{ font-size:8pt; color:Gray;  cursor:pointer; padding:2px; vertical-align:bottom; display:inline-block; }
            .ZR		{ font-size:8pt; color:Gray;  cursor:pointer; padding:2px; vertical-align:bottom; display:inline-block; }
            .ZA		{ font-size:8pt; color:Gray;  cursor:pointer; padding:2px; vertical-align:bottom; display:inline-block; }
                        
            .ZDSel  { font-size:8pt; color:Black;  cursor:pointer; padding:2px; vertical-align:bottom; border:1px solid #FFF99D; font-weight:bold; display:inline-block; /*background-color:#FFF99D;*/ }
            .ZPSel  { font-size:8pt; color:Black;  cursor:pointer; padding:2px; vertical-align:bottom; border:1px solid #8DCFF4; font-weight:bold; display:inline-block; /*background-color:#8DCFF4;*/ }
            .ZQSel  { font-size:8pt; color:Black;  cursor:pointer; padding:2px; vertical-align:bottom; border:1px solid #F4948D; font-weight:bold; display:inline-block; /*background-color:#F4948D;*/ }
            .ZHSel  { font-size:8pt; color:Black;  cursor:pointer; padding:2px; vertical-align:bottom; border:1px solid #9595C6; font-weight:bold; display:inline-block; /*background-color:#9595C6;*/ }
            .ZCSel  { font-size:8pt; color:Black;  cursor:pointer; padding:2px; vertical-align:bottom; border:1px solid #A8D59D; font-weight:bold; display:inline-block; /*background-color:#A8D59D;*/ }
            .ZRSel  { font-size:8pt; color:Black;  cursor:pointer; padding:2px; vertical-align:bottom; border:1px solid #FFD49D; font-weight:bold; display:inline-block; /*background-color:#FFD49D;*/ }
            .ZASel  { font-size:8pt; color:Black;  cursor:pointer; padding:2px; vertical-align:bottom; border:1px solid #FF9294; font-weight:bold; display:inline-block; /*background-color:#FF9294;*/ }
                        
            .formfieldview { font-size:9pt; border: none;}
            .formFieldViewDate { font-size:7pt; border: none; color:red; }
            .formfieldinput       { font-size:9pt; border: solid 1px #ffffff; }
            .Plain { font-size:9pt; border: none; }
            P {margin-top:2px; margin-bottom:2px;}
                        
            /*Ms*/
            v\\:* { BEHAVIOR: url(#default#VML) }
            o\\:* { BEHAVIOR: url(#default#VML) }
            x\\:* { VISIBILITY: hidden; POSITION: relative }
            .shape { BEHAVIOR: url(#default#VML) }
            
            #editor, #EDITOR { border: 0; padding: 15px 8px 0 10px; max-width:708px; overflow:hidden; overflow-x:auto;} 
                        
            /* 첨언처리 */
            .app_opi {width:100%;border:0px solid red;padding:5px 0px 0px 0px;margin:0px;/*font-size:11px;*/}
            .app_opi .tit {padding:3px 0px;}
            .app_opi .date {padding:3px 0px 3px 5px; /*width:auto;float:right;top:0px;*/}
            .app_opi .cont {width:100%;padding:5px 5px;}
            
                    
                        
            /* 양식공통사용 */
            .outlineB { FONT-SIZE: 12px; FONT-FAMILY: gulim; WIDTH: 100%; }
            .outlineB tr {height:30px;}
            .outlineB th {text-align:center;background:#f5ebe1;color:#662800;}
            .outlineB td {text-align:center;}
                        
            .outlineA { FONT-SIZE: 12px; FONT-FAMILY: gulim; WIDTH: 100%; }
            .outlineA tr {height:30px;}
            .outlineA th {text-align:left;padding-left:3px;background:#f5ebe1;color:#662800;}
            .outlineA td {padding-left:2px;}
                        
            .subtit {font-weight:bold;padding:5px;}
            .gap3 {height:3px;}
            .gap5 {height:5px;}
            .gap10 {height:10px;}
            .gap20 {height:20px;}
            select {border:1px solid #ccc; font-size:12px;}
            """;

    private static final String VIEW_INLINE_CSS = """
        BODY  { font-family: gulim; font-size:10pt; color: #000000; margin:0px; padding:0px; line-height: 140%;
                scrollbar-face-color: #F2F2F2; scrollbar-shadow-color:#999999; scrollbar-highlight-color:#999999;
                scrollbar-3dlight-color: #FFFFFF; scrollbar-darkshadow-color: #FFFFFF; scrollbar-track-color: #F6F6F6;
                scrollbar-arrow-color: #999999; }
        TABLE { font-family: gulim; font-size:9pt; }
        INPUT { font-size:9pt; border: solid 1px #ffffff; }

        .ZD { font-size:8pt; }
        .ZP { font-size:8pt; }
        .ZQ { font-size:8pt; }
        .ZH { font-size:8pt; }
        .ZC { font-size:8pt; }
        .ZR { font-size:8pt; }
        .formfieldview        { font-size:9pt; border: none; }
        .formFieldViewDate    { font-size:7pt; border: none; color:red; }
        .formfieldinput       { font-size:9pt; border: solid 1px #ffffff; }
        .Plain                { font-size:9pt; border: none; }
        P                     { margin-top:2px; margin-bottom:2px; }

        .ZDBG { background-color:#FFF99D; font-size:8pt; }
        .ZPBG { background-color:#8DCFF4; font-size:8pt; }
        .ZQBG { background-color:#F4948D; font-size:8pt; }
        .ZHBG { background-color:#9595C6; font-size:8pt; }
        .ZCBG { background-color:#A8D59D; font-size:8pt; }
        .ZRBG { background-color:#FFD49D; font-size:8pt; }
        .ZABG { background-color:#FF9294; font-size:8pt; }

        .C     { background-color:#99CC66; }
        .H     { background-color:#8080FF; }
        .I     { background-color:#FF9900; }
        .P     { background-color:#FF9900; }
        .R     { background-color:#FF4455; }
        .W     { background-color:#FFFF00; }
        .D     { background-color:#8080FF; }
        .Z     { background-color:#FF4455; }

        .Dsc    { background-color:#E9E9E9; border:solid #808080 1px; }
        .DscMy  { background-color:#E9E9E9; border:solid #FF0000 1px; }
        .DscZR  { background-color:#E8E4BB; border:solid #808080 1px; }
        .DscMyZR{ background-color:#E8E4BB; border:solid #FF0000 1px; }
        .stateLine    { border-right: solid #000000 1px; }
        .stateLineSub { border-right: solid #000000 1px; }
        .Doc   { background-color:#EEF2F5; border-left: solid #000000 1px; border-right: solid #000000 1px; font-size:9pt; }
        .DocMo { background-color:#EEF2F5; border-left: solid #000000 1px; border-right: solid #000000 1px; }

        /*Ms*/
        v\\:*    { BEHAVIOR: url(#default#VML) }
        o\\:*    { BEHAVIOR: url(#default#VML) }
        x\\:*    { VISIBILITY: hidden; POSITION: relative }
        .shape   { BEHAVIOR: url(#default#VML) }

        /* 첨언처리 */
        .app_opi            { width:100%; border:0; padding:5px 0 0 0; margin:0; }
        .app_opi .tit       { padding:3px 0; }
        .app_opi .date      { padding:3px 0 3px 5px; }
        .app_opi .cont      { width:100%; padding:5px; }

        #editor, #EDITOR { border: 0; padding: 15px 8px 0 10px; max-width:708px; overflow:hidden; overflow-x:auto;}

        .tabprt  { table-layout:fixed; float:left; width:100%; padding:0;
                   border:1px solid #c0c0c0; border-collapse:collapse; }
        .tabprt th{ font-weight:normal; font-size:12px; border-right:1px solid #c0c0c0;
                    min-width:30px; text-align:center; }
        .tabprt td{ font-weight:normal; font-size:12px; border-bottom:1px solid #c0c0c0;
                    vertical-align:top; min-width:30px; padding:5px; }

        .outlineB { font-size:12px; font-family:gulim; width:100%; }
        .outlineB tr { height:30px; }
        .outlineB th{ text-align:center; background:#f5ebe1; color:#662800; }
        .outlineB td{ text-align:center; }

        .outlineA { font-size:12px; font-family:gulim; width:100%; }
        .outlineA tr { height:30px; }
        .outlineA th{ text-align:left; padding-left:3px; background:#f5ebe1; color:#662800; }
        .outlineA td{ padding-left:2px; }

        .subtit  { font-weight:bold; padding:5px; }
        .gap3    { height:3px; }
        .gap5    { height:5px; }
        .gap10   { height:10px; }
        .gap20   { height:20px; }
        select   { border:1px solid #ccc; font-size:12px; }
    """;

    public static String transform(String html) {
        if (!StringUtils.hasText(html)) return html;

        Document doc = Jsoup.parse(html);

        transformLinkCss(doc);
        transformImageSrc(doc);
        enhanceEditorImageStyle(doc);
        removeOnclick(doc);

        return doc.html();
    }

    private static void transformLinkCss(Document doc) {
        for (Element link : doc.select("link[href]")) {
            String rel = link.attr("rel").toLowerCase();
            String href = link.attr("href");

            if (rel.contains("stylesheet") && href != null && href.toLowerCase().contains("func_approvalview.css")) {
                link.remove();

                Element style = doc.createElement("style")
                        .attr("type", "text/css")
                        .appendText(VIEW_INLINE_CSS);
                // 문서 <head>가 없을 땐 <body> 최상단으로
                if (doc.head() != null) {
                    doc.head().appendChild(style);
                } else {
                    doc.body().insertChildren(0, style);
                }
            }else if((rel.contains("stylesheet") && href != null && href.toLowerCase().contains("func_approvaledit.css"))){
                link.remove();

                Element style = doc.createElement("style")
                        .attr("type", "text/css")
                        .appendText(EDIT_INLINT_CSS);
                // 문서 <head>가 없을 땐 <body> 최상단으로
                if (doc.head() != null) {
                    doc.head().appendChild(style);
                } else {
                    doc.body().insertChildren(0, style);
                }
            }
        }
    }

    private static void transformImageSrc(Document doc) {
        for (Element img : doc.select("img")) {
            String src = img.attr("src");

            src = src.replaceAll("[?&]number=[^&]*", "");
            src = src.replaceAll("[?&]$", "");
            src = src.replaceAll("\\?&", "?");

            if (src.contains("/FileData/")) {
                String path = src.substring(src.indexOf("/FileData/"));
                img.attr("src", "/doc/file/preview?path=DeskPlusFileServer" + path);
            } else if (src.contains("/Data/Editor/XFE2/upload/")) {
                String path = src.substring(src.indexOf("/Data/Editor/XFE2/upload/"))
                        .replace("/Data/Editor/XFE2/upload/", "");
                img.attr("src", "/doc/file/preview?path=DeskPlusFileServer/XFE2Upload/" + path);
            }
        }
    }

    private static void enhanceEditorImageStyle(Document doc) {

        for (Element editorDiv : doc.select("div[id$=EDITOR]")) {
            String originalId = editorDiv.id();
            editorDiv.attr("id", "EDITOR");

            String style = editorDiv.attr("style");

            if (style.contains("width:100%")) {
                String replaced = style.replace("width: 100%;", "width: auto;");
                editorDiv.attr("style", replaced);
            }else{
                editorDiv.attr("style", style + (style.endsWith(";") || style.isEmpty() ? "" : ";") + "width:auto;");
            }

            for (Element img : editorDiv.select("img")) {
                String imgStyle = img.attr("style");
                if (!imgStyle.contains("max-width")) {
                    img.attr("style", imgStyle + (imgStyle.endsWith(";") || imgStyle.isEmpty() ? "" : ";") + "max-width:99.9%;");
                }
            }
        }

        for (Element classDiv : doc.select("div[class$=formfieldinput]")) {
            Set<String> classes = classDiv.classNames();
            if (!classes.contains("formfieldinput")) {
                classDiv.addClass("formfieldinput");
            }
        }
    }

    private static void removeOnclick(Document doc) {
        for (Element span : doc.select("span[onclick]")) {
            span.removeAttr("onclick");
        }
    }
}
