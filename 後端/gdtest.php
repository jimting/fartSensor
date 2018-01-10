<?php
$img_gao=170;  
$img_kuan=0;  
$jiange=30;//横坐标点与点之间的间隔，生成的图片宽度会根据传入数据的多少而自动变化  
$zuo=20;//左侧留空  
$you=20;//右侧留空  
$shang=20;//上留空  
$xia=20;//下留空  
$zuidashujuzhi=1;  
$p_x = array();//点横坐标  
$p_y = array();//点纵坐标  
$y_name=split(",",$_GET["x_name"]);  
if ($_GET["a"]=="") die("error id:0");  
$shuju=split(",",$_GET["a"]);  
//得到纵轴最大值  
for($i=0;$i<count($shuju);$i++){  
if(!is_numeric($shuju[$i])) die("error id:1");  
if($shuju[$i]>$zuidashujuzhi) $zuidashujuzhi=$shuju[$i];  
}  
//得到图像宽度   
$img_kuan=$zuo+$you+count($shuju)*$jiange;  
//然后创建图像资源   
$image = imagecreate($img_kuan,$img_gao);  
//灰色背景  
$white = imagecolorallocate($image, 0xEE, 0xEE, 0xEE);  
//坐标轴用黑色显示  
$zuobiao_yanse = imagecolorallocate($image, 0x00, 0x00, 0x00);  
//折线用蓝色显示  
$xian_yanse = imagecolorallocate($image, 0x00, 0x00, 0xFF);  
//画坐标  
//横轴  
imageline ( $image, $zuo, $img_gao-$xia, $img_kuan-$you/2, $img_gao-$xia, $zuobiao_yanse);  
//纵轴  
imageline ( $image, $zuo, $shang/2, $zuo, $img_gao-$xia, $zuobiao_yanse);  
  
//得到每个点的坐标  
for($i=0;$i<count($shuju);$i++){  
array_push ($p_x, $zuo+$i*$jiange);  
array_push ($p_y, $shang+round(($img_gao-$shang-$xia)*(1-$shuju[$i]/$zuidashujuzhi)));  
}  
  
//纵轴刻度  
imageline ( $image, $zuo, $shang, $zuo+6, $shang, $zuobiao_yanse);  
imagestring ( $image, 1, $zuo/4, $shang,$zuidashujuzhi, $zuobiao_yanse);  
imageline ( $image, $zuo, $shang+($img_gao-$shang-$xia)*1/4, $zuo+6, $shang+($img_gao-$shang-$xia)*1/4, $zuobiao_yanse);  
imagestring ( $image, 1, $zuo/4, $shang+($img_gao-$shang-$xia)*1/4,$zuidashujuzhi*3/4, $zuobiao_yanse);  
imageline ( $image, $zuo, $shang+($img_gao-$shang-$xia)*2/4, $zuo+6, $shang+($img_gao-$shang-$xia)*2/4, $zuobiao_yanse);  
imagestring ( $image, 1, $zuo/4, $shang+($img_gao-$shang-$xia)*2/4,$zuidashujuzhi*2/4, $zuobiao_yanse);  
imageline ( $image, $zuo, $shang+($img_gao-$shang-$xia)*3/4, $zuo+6, $shang+($img_gao-$shang-$xia)*3/4, $zuobiao_yanse);  
imagestring ( $image, 1, $zuo/4, $shang+($img_gao-$shang-$xia)*3/4,$zuidashujuzhi*1/4, $zuobiao_yanse);  
  
//横轴刻度  
for($i=0;$i<count($shuju);$i++){  
imageline ( $image, $zuo+$i*$jiange, $img_gao-$xia, $zuo+$i*$jiange, $img_gao-$xia-6, $zuobiao_yanse);  
imagestring ( $image, 1, $zuo+$i*$jiange-$jiange/4, $shang+($img_gao-$shang-$xia)+2,$y_name[$i], $zuobiao_yanse);  
}  
  
  
//折线  
$shuju_yanse_int=0;  
for($i=0;$i<count($shuju);$i++){  
if($i+1<>count($shuju)){  
imageline ( $image, $p_x[$i], $p_y[$i], $p_x[$i+1], $p_y[$i+1], $xian_yanse);  
imagefilledrectangle($image, $p_x[$i]-1, $p_y[$i]-1, $p_x[$i]+1, $p_y[$i]+1, $xian_yanse);  
}  
}  
//上一个循环没有画出最后一个点效果，这里还要追加  
imagefilledrectangle($image, $p_x[count($shuju)-1]-1, $p_y[count($shuju)-1]-1, $p_x[count($shuju)-1]+1, $p_y[count($shuju)-1]+1, $xian_yanse);  
  
//标注数据值  
for($i=0;$i<count($shuju);$i++){  
imagestring ( $image, 3, $p_x[$i]+4, $p_y[$i]-12,$shuju[$i], $zuobiao_yanse);  
}  
//设定文件头   
header('Content-type: image/png');  
//输出图像  
imagepng($image);  
//释放资源   
imagedestroy($image);  
?>