package com.pda.patrol.baseclass;

import androidx.core.content.FileProvider;

import com.pda.patrol.R;

public class MyFileProvider extends FileProvider {
   public MyFileProvider() {
       super(R.xml.file_paths);
   }
}