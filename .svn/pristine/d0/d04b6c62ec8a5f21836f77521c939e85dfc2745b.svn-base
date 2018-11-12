package com.greatchn.common.utils;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Md5 算法加密工具类(已做过混淆处理)
 * </p>
 * 
 * @author ZLi 2015-8-9
 * 
 */
public final class MD5Util {
	protected static final int BUFFER_SIZE = 8096;
	/**
	 * <p>
	 * 获取加密串
	 * </p>
	 * 
	 * @param s 要混淆的字符
	 * @return @author ZLi 2016-1-19
	 */
	public static String MD5(String s) {
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
				'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char[] str = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	 public static String SHA1(String decrypt) throws DigestException {  
	        //获取信息摘要 - 参数字典排序后字符串  
	        try {  
	            //指定sha1算法  
	            MessageDigest digest = MessageDigest.getInstance("SHA-1");  
	            digest.update(decrypt.getBytes());  
	            //获取字节数组  
	            byte[] messageDigest = digest.digest();
	            // Create Hex String  
	            StringBuffer hexString = new StringBuffer();  
	            // 字节数组转换为 十六进制 数  
	            for (int i = 0; i < messageDigest.length; i++) {  
	                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);  
	                if (shaHex.length() < 2) {  
	                    hexString.append(0);  
	                }  
	                hexString.append(shaHex);  
	            }  
	            return hexString.toString().toUpperCase();  
	  
	        } catch (NoSuchAlgorithmException e) {  
	            e.printStackTrace();  
	            throw new DigestException("签名错误！");  
	        }  
	    }  
	    /** 
	     * 获取参数的字典排序 
	     * @param maps 参数key-value map集合 
	     * @return String 排序后的字符串 
	     */  
	    @SuppressWarnings("unused")
		private static String getOrderByLexicographic(Map<String,Object> maps){  
	        return splitParams(lexicographicOrder(getParamsName(maps)),maps);  
	    }  
	    /** 
	     * 获取参数名称 key 
	     * @param maps 参数key-value map集合 
	     */
	    private static List<String> getParamsName(Map<String,Object> maps){  
	        List<String> paramNames = new ArrayList<String>();  
	        for(Map.Entry<String,Object> entry : maps.entrySet()){  
	            paramNames.add(entry.getKey());  
	        }  
	        return paramNames;  
	    }  
	    /** 
	     * 参数名称按字典排序 
	     * @param paramNames 参数名称List集合 
	     * @return 排序后的参数名称List集合 
	     */  
	    private static List<String> lexicographicOrder(List<String> paramNames){  
	        Collections.sort(paramNames);  
	        return paramNames;  
	    }  
	    /** 
	     * 拼接排序好的参数名称和参数值 
	     * @param paramNames 排序后的参数名称集合 
	     * @param maps 参数key-value map集合 
	     * @return String 拼接后的字符串 
	     */  
	    private static String splitParams(List<String> paramNames,Map<String,Object> maps){  
	        StringBuilder paramStr = new StringBuilder();  
	        for(String paramName : paramNames){  
	            paramStr.append(paramName);  
	            for(Map.Entry<String,Object> entry : maps.entrySet()){  
	                if(paramName.equals(entry.getKey())){  
	                    paramStr.append(String.valueOf(entry.getValue()));  
	                }  
	            }  
	        }  
	        return paramStr.toString();  
	    }  
	
}
