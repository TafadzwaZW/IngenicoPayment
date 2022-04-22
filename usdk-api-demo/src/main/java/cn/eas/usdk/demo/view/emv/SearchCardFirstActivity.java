package cn.eas.usdk.demo.view.emv;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.usdk.apiservice.aidl.emv.EMVData;
import com.usdk.apiservice.aidl.emv.SearchCardListener;
import com.usdk.apiservice.aidl.emv.WaitCardFlag;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;

/**
 * Start the EMV process using the startEMV interface
 * Search the card first and start the EMV process according to the card type.
 */
public class SearchCardFirstActivity extends BaseEMVActivity {

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		super.onCreateView(savedInstanceState);
		setTitle("searchCard >> startEMV");
	}

	public void startTrade(View v) {
		outputBlackText("\n>>>>>>>>>> start trade <<<<<<<<<");
		outputBlueText("******  search card ******");
        outputRedText(getString(R.string.insert_pass_swipe_card));

        try {
            emv.searchCard(cardOption.toBundle(), DemoConfig.TIMEOUT, new SearchCardListener.Stub() {
                @Override
                public void onCardPass(int cardType) {
                    outputText("=> onCardPass | cardType = " + cardType);
                    startEMV(emvOption.flagPSE((byte)0x01));
                }

                @Override
                public void onCardInsert() {
                    outputText("=> onCardInsert");
                    startEMV(emvOption.flagPSE((byte)0x00));
                }

                @Override
                public void onCardSwiped(Bundle track) {
                    outputText("=> onCardSwiped");
                    outputText("==> Pan: " + track.getString(EMVData.PAN));
                    outputText("==> Track 1: " + track.getString(EMVData.TRACK1));
                    outputText("==> Track 2: " + track.getString(EMVData.TRACK2));
                    outputText("==> Track 3: " + track.getString(EMVData.TRACK3));
                    outputText("==> Service code: " + track.getString(EMVData.SERVICE_CODE));
                    outputText("==> Card exprited date: " + track.getString(EMVData.EXPIRED_DATE));

                    int[] trackStates = track.getIntArray(EMVData.TRACK_STATES);
                    for(int i = 0; i < trackStates.length; i++) {
                        outputText(String.format("==> Track %s state: %d", i+1, trackStates[i]));
                    }
                }

                @Override
                public void onTimeout() {
                    outputRedText("=> onTimeout");
                }

                @Override
                public void onError(int code, String message) {
                    outputRedText(String.format("=> onError | %s[0x%02X]", message, code));
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

	public void stopTrade(View v) {
		outputBlackText("\n>>>>>>>>>> stop trade <<<<<<<<<");
		stopEMV();
		stopSearch();
        halt();
	}

	@Override
	public void doWaitCard(int flag) throws RemoteException {
		outputText("=> onWaitCard | flag = " + flag);
		switch (flag) {
			case WaitCardFlag.NORMAL:
				outputRedText("!!! NORMAL flag will not happen when call searchCard first !!!");
				break;

			default:
				super.doWaitCard(flag);
		}
	}
}
