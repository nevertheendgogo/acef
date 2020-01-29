package com.huangchao.acef.utils;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 本类用于自动生成项目所需文件夹
 * 
 * @author 巧合注定
 *
 */
@Component
public class MyCommandLineRunner implements CommandLineRunner {

	@Autowired
	private SystemConfig systemConfig;

	@Override
	public void run(String... args) throws Exception {
		String basePath = systemConfig.filePath;

		// 创建根目录
		File file = new File(basePath);
		if (!file.exists()) {// 如果文件夹不存在
			file.mkdir();// 创建文件夹
		}

		for (int i = 0; i < systemConfig.folder.length; i++) {
			file = new File(basePath + systemConfig.folder[i]);
			System.out.println(file.getAbsolutePath());
			if (!file.exists()) {// 如果文件夹不存在
				file.mkdir();// 创建文件夹
			}
		}

	}

}
