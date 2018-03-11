import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Starry Starry Night
 * @since 2018-03-11
 */
@Slf4j
public class Test {

  @org.junit.Test
  public void test() {
    final BayesSmoothingExecutor executor = new BayesSmoothingExecutor(1e-7, 100000);
    log.info("start generating bayes smoothing...");

    long t1 = System.currentTimeMillis();
    List<CreativeAdUnitRecord> recordList = new ArrayList<>();
    final List<BayesSmoothingResult> resultList = recordList
        .stream()
        .collect(Collectors.groupingBy(CreativeAdUnitRecord::getAdUnitId, Collectors.toList()))
        .entrySet()
        .stream()
        .parallel()
        .map(entry -> {
          final List<RecordEntry> samples = entry.getValue()
              .stream()
              //同一广告位下的样本按照创意ID进行聚合
              //.collect(Collectors.groupingBy(CreativeAdUnitRecord::getCreativeId,
              //    Collectors.mapping(CreativeAdUnitRecord::getEntry,
              //        Collectors.reducing(RecordEntry.of(), RecordEntry::combine))
              //))
              .collect(Collectors.groupingBy(CreativeAdUnitRecord::getCreativeId,
                  Collectors.mapping(CreativeAdUnitRecord::getEntry,
                      Collectors.reducing(RecordEntry.of(), RecordEntry::combine))
              ))
              .entrySet()
              .stream()
              .map(Map.Entry::getValue)
              //对样本进行过滤
              .filter(re -> {
                long clickCount = re.getClickCount();
                long impressionCount = re.getImpressionCount();
                return clickCount > 5 && impressionCount > 10000 && clickCount < impressionCount;
              })
              .collect(Collectors.toList());
          return new AbstractMap.SimpleEntry<>(entry.getKey(), samples);
        })
        .filter(entry -> entry.getValue().size() > 3)
        .map(entry -> executor.executeSmoothing(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
    long t2 = System.currentTimeMillis();
  }
}
