import java.util.Map;
import lombok.Data;

@Data
public class CreativeAdUnitRecord {
  private String adUnitId;
  private String value;
  private Long creativeId;
  private Map entry;
}
