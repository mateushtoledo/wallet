package com.toledo.wallet.system.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static String toIsoDate(Date dt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(dt);
	}
}
