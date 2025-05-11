package com.deustocoches.performance;

import com.deustocoches.repository.PromocionRepository;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PromocionPerformanceTest {

    @Rule
    public ContiPerfRule i = new ContiPerfRule();

    @Autowired
    private PromocionRepository promocionRepository;

    @Test
    @PerfTest(invocations = 100, threads = 10)
    @Required(average = 200, max = 500)
    public void testPromocionRepositoryFindAllPerformance() {
        promocionRepository.findAll();
    }
}