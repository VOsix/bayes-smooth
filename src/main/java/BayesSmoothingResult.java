import java.io.Serializable;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import lombok.extern.slf4j.Slf4j;

@Immutable
@Slf4j
public final class BayesSmoothingResult implements Serializable {

  private static final long serialVersionUID = 2353494519143065104L;

  private final long groupId;
  private final double alpha;
  private final double beta;

  public BayesSmoothingResult(long groupId, double alpha, double beta) {
    this.groupId = groupId;
    this.alpha = alpha;
    this.beta = beta;
  }

  public long getGroupId() {
    return groupId;
  }

  public double getAlpha() {
    return alpha;
  }

  public double getBeta() {
    return beta;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BayesSmoothingResult result = (BayesSmoothingResult) o;

    if (groupId != result.groupId) {
      return false;
    }
    return Double.compare(result.alpha, alpha) == 0 && Double.compare(result.beta, beta) == 0;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = (int) (groupId ^ (groupId >>> 32));
    temp = Double.doubleToLongBits(alpha);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(beta);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public String toString() {

    return "groupId" + groupId +
        "alpha" + alpha +
        "beta" + beta;
    //return MoreObjects.toStringHelper(this)
    //    .add("groupId", groupId)
    //    .add("alpha", alpha)
    //    .add("beta", beta)
    //    .toString();
  }

  public double smoothingCtrOf(@Nonnull RecordEntry entry) {
    return (alpha + entry.getClickCount()) / (alpha + beta + entry.getImpressionCount());
  }
}
