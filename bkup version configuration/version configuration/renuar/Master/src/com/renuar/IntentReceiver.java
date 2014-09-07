/*
Copyright 2009-2011 Urban Airship Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE URBAN AIRSHIP INC ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL URBAN AIRSHIP INC OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.renuar;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.renuar.utils.MximoFlurryAgent;
import com.urbanairship.UAirship;
import com.urbanairship.push.GCMMessageHandler;
import com.urbanairship.push.PushManager;

public class IntentReceiver extends BroadcastReceiver {

	public static String APID_UPDATED_ACTION_SUFFIX = ".apid.updated";

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();

		if (action.equals(PushManager.ACTION_PUSH_RECEIVED)) {
			MximoFlurryAgent
					.logEvent(MximoFlurryAgent.PUSH_NOTIFICATION_RECIVED);
			int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);
			MximoLogPushExtras(intent);

		} else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {

			MximoLogPushExtras(intent);

			Intent launch = new Intent(Intent.ACTION_MAIN);
			launch.setClass(UAirship.shared().getApplicationContext(),
					Splash.class);
			launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			launch.putExtra(Splash.KEY_IS_FROM_NOTIFICATION, true);
			UAirship.shared().getApplicationContext().startActivity(launch);

		} else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {

			// Notify any app-specific listeners
			Intent launch = new Intent(UAirship.getPackageName()
					+ APID_UPDATED_ACTION_SUFFIX);
			UAirship.shared().getApplicationContext().sendBroadcast(launch);

		} else if (action.equals(GCMMessageHandler.ACTION_GCM_DELETED_MESSAGES)) {

		}

	}

	/**
	 * MximoLog the values sent in the payload's "extra" dictionary.
	 * 
	 * @param intent
	 *            A PushManager.ACTION_NOTIFICATION_OPENED or
	 *            ACTION_PUSH_RECEIVED intent.
	 */
	private void MximoLogPushExtras(Intent intent) {
		Set<String> keys = intent.getExtras().keySet();
		for (String key : keys) {

			// ignore standard C2DM extra keys
			List<String> ignoredKeys = (List<String>) Arrays.asList(
					"collapse_key",// c2dm collapse key
					"from",// c2dm sender
					PushManager.EXTRA_NOTIFICATION_ID,// int id of generated
														// notification
														// (ACTION_PUSH_RECEIVED
														// only)
					PushManager.EXTRA_PUSH_ID,// internal UA push id
					PushManager.EXTRA_ALERT);// ignore alert
			if (ignoredKeys.contains(key)) {
				continue;
			}

		}
	}
}
