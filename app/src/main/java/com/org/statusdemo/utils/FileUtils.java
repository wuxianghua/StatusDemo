package com.org.statusdemo.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Created by zhang on 2015/4/29.
 */
public class FileUtils {

    /*
    *  检测SDCard是否存在
    * */
    public static boolean checkoutSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public  static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {                  //文件存在时
                InputStream inStream = new FileInputStream(oldPath);      //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;            //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }  catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    /*
    *  将asserts下一个指定文件夹中所有文件copy到SDCard中
    * */
    public static void copyDirToSDCardFromAsserts(Context context, String dirName, String dirName2, boolean isCover) {
        try {
            AssetManager assetManager = context.getAssets();
            String[] fileList = assetManager.list(dirName2);
            outputStr(dirName2, fileList); // 输出dirName2中文件名
            String dir = Environment.getExternalStorageDirectory() + File.separator + dirName;

            if (fileList != null && fileList.length > 0) {
                File file = null;

                // 创建文件夹
                file = new File(dir);
                if (!file.exists()) {
                    file.mkdirs();
                } else {
                    Log.w("FileUtils", dir + "已存在.");
                }

                // 创建文件
                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                byte[] buffer = new byte[1024];
                int len = -1;
                for (int i = 0; i < fileList.length; i++) {
                    file = new File(dir, fileList[i]);
                    if (!file.exists()) {
                        file.createNewFile();
                    } else {
                        if (!isCover){
                            Log.w("FileUtils", "没有复制:" + file.getName());
                            continue;
                        }
                    }
                    inputStream = assetManager.open(dirName2 + File.separator + fileList[i]);
                    outputStream = new FileOutputStream(file);
                    while ((len = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }
                    outputStream.flush();
                }
                // 关流
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.e("FileUtils", "IOException e");
            e.printStackTrace();
        }
    }

    /*
    * 输出String[]中内容
    * 作用：输出文件夹中文件名
    * */
    public static void outputStr(String dirName, String[] listStr) {
        if (listStr != null) {
            if (listStr.length <= 0) {
                Log.w("FileUtils", dirName + "文件为空");
            } else {
                Log.w("FileUtils", dirName + "文件中有以下文件：");
                for (String str : listStr) {
                    Log.w("FileUtils", str);
                }
            }
        }
    }

    public static final String SDPATH= Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separatorChar;

    /**
     * 外置存储卡的路径
     * @return
     */
    public static String getExternalStorePath() {
        if (isExistExternalStore()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 是否有外存卡
     * @return
     */
    public static boolean isExistExternalStore() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkSDcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    public static String readFileFromAssets(Context context, String name) {
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(name);
        } catch (Exception e) {
            throw new RuntimeException(FileUtils.class.getName()
                    + ".readFileFromAssets---->" + name + " not found");
        }
        return inputStream2String(is);
    }

    public static String inputStream2String(InputStream is) {
        if (null == is) {
            return null;
        }
        StringBuilder resultSb = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            resultSb = new StringBuilder();
            String len;
            while (null != (len = br.readLine())) {
                resultSb.append(len);
            }
        } catch (Exception ex) {
        } finally {
            IOUtils.closeIO(is);
        }
        return null == resultSb ? null : resultSb.toString();
    }

    public static boolean bitmapToFile(Bitmap bitmap, String filePath) {
        boolean isSuccess = false;
        if (bitmap == null)
            return isSuccess;
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(filePath),
                    8 * 1024);
            isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeIO(out);
        }
        return isSuccess;
    }

    public static String loadFromAssets(Context context,String fileName) {
        InputStream inputStream = null;
        try {
            AssetManager assets = context.getAssets();
            inputStream = assets.open(fileName);
            InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                builder.append(str);
                builder.append("\n");
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Bitmap getBitmapFromSd(String fileName) {
        return BitmapFactory.decodeFile(SDPATH + fileName);
    }

    public static String getSavePath(String folderName) {
        return createSDDir(folderName).getAbsolutePath();
    }

    public static File getSaveFolder(String folderName) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsoluteFile()
                + File.separator
                + folderName
                + File.separator);
        file.mkdirs();
        return file;
    }

    public static File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public static File createSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        if (!dir.exists()) {
            dir.mkdirs();
            LogUtil.d("createSDDir:" + dirName);
        }
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    public static File write2SDFromInput(String path, String fileName,
                                         InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            createSDDir(path);
            file = createSDFile(path + fileName);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[input.available()];
            while ((input.read(buffer)) != -1) {
                output.write(buffer);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    public static void writeFile2Sd(String filepath, String content) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(filepath);
            byte[] bytes = content.getBytes();
            fout.write(bytes);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void appendFile2Sd(String filepath, String content) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(filepath, true);
            byte[] bytes = content.getBytes();
            fout.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveFileCache(byte[] fileData, String folderPath,
                                     String fileName) {
        File folder = new File(folderPath);
        folder.mkdirs();
        File file = new File(folderPath, fileName);
        ByteArrayInputStream is = new ByteArrayInputStream(fileData);
        OutputStream os = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
                os = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = is.read(buffer))) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            } catch (Exception e) {
                throw new RuntimeException(
                        FileUtils.class.getClass().getName(), e);
            } finally {
                IOUtils.closeIO(is, os);
            }
        }
    }

    public static final byte[] input2byte(InputStream inStream) {
        if (inStream == null)
            return null;
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int rc = 0;
        try {
            while ((rc = inStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    /**
     * 根据文件名和后缀 拷贝文件
     * @param fileDir
     * @param fileName
     * @param ext
     * @param buffer
     * @return
     */
    public static int copyFile(String fileDir , String fileName , String ext , byte[] buffer) {
        return copyFile(fileDir, fileName + ext, buffer);
    }

    /**
     * 拷贝文件
     * @param fileDir
     * @param fileName
     * @param buffer
     * @return
     */
    public static int copyFile(String fileDir , String fileName , byte[] buffer) {
        if(buffer == null) {
            return -2;
        }

        try {
            File file = new File(fileDir);
            if(!file.exists()) {
                file.mkdirs();
            }
            File resultFile = new File(file, fileName);
            if(!resultFile.exists()) {
                resultFile.createNewFile();
            }
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(resultFile, true));
            bufferedOutputStream.write(buffer);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            return 0;

        } catch (Exception e) {
        }
        return -1;
    }

    /**
     *
     * @param filePath
     * @param seek
     * @param length
     * @return
     */
    public static byte[] readFlieToByte (String filePath , int seek , int length) {
        if(TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if(!file.exists()) {
            return null;
        }
        if(length == -1) {
            length = (int)file.length();
        }

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            byte[] bs = new byte[length];
            randomAccessFile.seek(seek);
            randomAccessFile.readFully(bs);
            randomAccessFile.close();
            return bs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取文件大小
     * decode file length
     * @param filePath
     * @return
     */
    public static int decodeFileLength(String filePath) {
        if(TextUtils.isEmpty(filePath)) {
            return 0;
        }
        File file = new File(filePath);
        if(!file.exists()) {
            return 0;
        }
        return (int)file.length();
    }

}
