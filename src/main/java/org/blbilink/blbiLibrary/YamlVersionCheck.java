package org.blbilink.blbiLibrary;

public class YamlVersionCheck {
    public static boolean checkYmlVersion(String version1, String version2) {
        // 分割版本号字符串
        String[] arr1 = version1.split("\\.");
        String[] arr2 = version2.split("\\.");

        // 比较版本号的每一部分
        for (int i = 0; i < Math.max(arr1.length, arr2.length); i++) {
            // 不足的部分视为0
            int num1 = i < arr1.length ? Integer.parseInt(arr1[i]) : 0;
            int num2 = i < arr2.length ? Integer.parseInt(arr2[i]) : 0;

            if (num1 > num2) {
                return true;
            } else if (num1 < num2) {
                return false;
            }
        }

        // 如果所有部分都相同，则认为版本相等
        return false;
    }
}
