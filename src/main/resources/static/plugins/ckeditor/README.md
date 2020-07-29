CKEditor 4
==========

Copyright (c) 2003-2016, CKSource - Frederico Knabben. All rights reserved.  
http://ckeditor.com - See LICENSE.md for license information.

CKEditor is a text editor to be used inside web pages. It's not a replacement
for desktop text editors like Word or OpenOffice, but a component to be used as
part of web applications and websites.

## Documentation

The full editor documentation is available online at the following address:
http://docs.ckeditor.com

## Installation

Installing CKEditor is an easy task. Just follow these simple steps:

 1. **Download** the latest version from the CKEditor website:
    http://ckeditor.com. You should have already completed this step, but be
    sure you have the very latest version.
 2. **Extract** (decompress) the downloaded file into the root of your website.

**Note:** CKEditor is by default installed in the `ckeditor` folder. You can
place the files in whichever you want though.

## Checking Your Installation

The editor comes with a few sample pages that can be used to verify that
installation proceeded properly. Take a look at the `samples` directory.

To test your installation, just call the following page at your website:

	http://<your site>/<CKEditor installation path>/samples/index.html

For example:

	http://www.example.com/ckeditor/samples/index.html
	
	
[修改图片上传预览 参考](https://blog.csdn.net/hffygc/article/details/83755459)<br/>
预览中有一堆火星文，可以修改相应配置删除它。
第一种方法：打开ckeditor/plugins/image/dialogs/image.js文件，搜索“b.config.image_previewText”，(b.config.image_previewText||'')单引号中的内容全删了，注意别删多了。(由于ckeditor的很多js文件都是压缩过的，格式很难看，很容易删错，所以不推荐此种方法)
第二种方法：打开config.js文件，加入下面一句话
config.image_previewText=' '; //预览区域显示内容

[显示图片上传 参考](https://blog.csdn.net/hffygc/article/details/83755459) <br/>
要想出现上传按钮，两种方法
第一种：还是刚才那个image.js
搜索“upload”可以找到这一段 id:'Upload',hidden:true，而我使用的4.3的是
id:"Upload",hidden:!0，反正改为false就行了，(遗憾的是此种方法对我这个版本不起作用)
第二种：打开config.js文件，加入下面一句话
config.filebrowserImageUploadUrl= "uploadImage"; //待会要上传的action或servlet


