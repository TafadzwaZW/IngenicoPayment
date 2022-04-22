package cn.eas.usdk.demo.view.emv;

import android.os.Bundle;
import android.view.View;

import com.usdk.apiservice.aidl.emv.CardType;
import com.usdk.apiservice.aidl.emv.WaitCardFlag;

import cn.eas.usdk.demo.R;

/**
 * Start the EMV process using the startProcess interface
 */
public class StartProcessActivity extends BaseEMVActivity {

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		super.onCreateView(savedInstanceState);
		setTitle("startProcess");
		bindViewById(R.id.swipeCBox).setVisibility(View.GONE);
		bindViewById(R.id.allRFCardCBox).setVisibility(View.GONE);
		bindViewById(R.id.loopSearchRFCard).setVisibility(View.GONE);
	}

	public void startTrade(View v) {
		outputBlackText("\n>>>>>>>>>> start trade <<<<<<<<<");
        outputBlueText("******  start Process ******");

        try {
            Bundle option = new Bundle();
            option.putAll(emvOption.toBundle());
            option.putAll(cardOption.toBundle());
			emv.startProcess(option, emvEventHandler);
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	public void stopTrade(View v) {
		outputBlackText("\n>>>>>>>>>> stop trade <<<<<<<<<");
        outputBlueText("******  stop EMV ******");
        try {
			outputResult(emv.stopProcess(), "=> Stop Process");
		} catch (Exception e) {
			handleException(e);
		}
	}

	@Override
	public void doWaitCard(int flag) {
		outputText("=> onWaitCard | flag = " + flag);
		switch(flag) {
			case WaitCardFlag.NORMAL:
				outputRedText(getString(R.string.insert_pass_card));
				break;
			case WaitCardFlag.SHOW_CARD_AGAIN:
				outputRedText(getString(R.string.pass_card_again));
				break;
			case WaitCardFlag.ISS_SCRIPT_UPDATE:
				outputRedText(getString(R.string.pass_card_for_script_update));
				break;
			case WaitCardFlag.EXECUTE_CDCVM:
				outputRedText(getString(R.string.pass_card_again_accord_prompt));
				break;
			default:
				outputRedText(getString(R.string.unknown_type));
				break;
		}
	}

	@Override
	public void doCardChecked(int cardType) {
		outputText("=> onCardChecked | cardType = " + cardType);
		switch (cardType) {
			case CardType.IC:
				outputBlueText(getString(R.string.insert_succ));
				break;

			case CardType.RF:
				outputBlueText(getString(R.string.tap_succ));
				break;

			default:
				outputRedText(getString(R.string.unknown_card_type));
				break;
		}
	}
}
