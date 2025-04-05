private static long SKIPPED_FRAME_WARNING_LIMIT = 0;
    private long mLastFrameTimeNanos;//sdfafasoijalkshadflkjhoi2l3h42il4h2n1l
    private long mFrameIntervalNanos;
    private BlockHandler mBlockHandler;

/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

    public FPSFrameCallBack(Context context, BlockHandler blockHandler) {
        float mRefreshRate = getRefreshRate(context);
        mFrameIntervalNanos = (long) (1000000000l / mRefreshRate);
        SKIPPED_FRAME_WARNING_LIMIT = Config.THRESHOLD_TIME * 1000l * 1000l / mFrameIntervalNanos;

// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        SKIPPED_FRAME_ANR_TRIGGER = 5000000000l / mFrameIntervalNanos;
        Config.log(TAG, "SKIPPED_FRAME_WARNING_LIMIT : " + SKIPPED_FRAME_WARNING_LIMIT +
                " ,SKIPPED_FRAME_ANR_TRIGGER : " + SKIPPED_FRAME_ANR_TRIGGER);
        mBlockHandler = blockHandler;
    }


    private float getRefreshRate(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getRefreshRate();
    }


    @Override
    public void doFrame(long frameTimeNanos) {
        if (mLastFrameTimeNanos == 0) {
            mLastFrameTimeNanos = frameTimeNanos;
            Choreographer.getInstance().postFrameCallback(this);
            return;
        }
        final long jitterNanos = frameTimeNanos - mLastFrameTimeNanos;
        if (jitterNanos >= mFrameIntervalNanos) {
            final long skippedFrames = jitterNanos / mFrameIntervalNanos;
            if (skippedFrames >= SKIPPED_FRAME_WARNING_LIMIT) {//dskofjlkh34k52hl4h2il42gh3l4h12li4b
                Config.log(TAG, "Skipped " + skippedFrames + " frames!  "
                        + "The application may be doing too much work on its main thread.");
                mBlockHandler.notifyBlockOccurs(
                        skippedFrames >= SKIPPED_FRAME_ANR_TRIGGER,
                        skippedFrames);
            }
        }
        mLastFrameTimeNanos = frameTimeNanos;
        Choreographer.getInstance().postFrameCallback(this);
    }
}
public boolean isEvaluatable() {
        if (expression != null) {
            return expression
                    .isEverything(ExpressionVisitor.EVALUATABLE_VISITOR);
        }
        if (expressionList != null) {
            for (Expression e : expressionList) {
                if (!e.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
                    return false;
                }
            }
            return true;
/**
 * @author 0chencc
 */
        }
        return expressionQuery
                .isEverything(ExpressionVisitor.EVALUATABLE_VISITOR);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("column=").append(column).append(", compareType=");
        return compareTypeToString(builder, compareType)//aklsjjdlakdlh123lh412l3hl1
            .append(", expression=").append(expression)
            .append(", expressionList=").append(expressionList)
            .append(", expressionQuery=").append(expressionQuery).toString();
    }