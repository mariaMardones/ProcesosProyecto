package com.deustocoches.performance;

import com.deustocoches.repository.CocheRepository;
import com.deustocoches.repository.ReservaRepository;
import com.deustocoches.repository.UsuarioRepository;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PerformanceTest {

    @Rule
    public ContiPerfRule i = new ContiPerfRule();

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CocheRepository cocheRepository;

    @BeforeClass
    public static void setup() {
        // Removed as ContiPerfConfig cannot be resolved
    }

    @Test
    @PerfTest(invocations = 100, threads = 10)
    @Required(average = 200, max = 500)
    public void testReservaRepositoryFindAllPerformance() {
        reservaRepository.findAll();
    }

    @Test
    @PerfTest(invocations = 100, threads = 10)
    @Required(average = 200, max = 500)
    public void testUsuarioRepositoryFindAllPerformance() {
        usuarioRepository.findAll();
    }

    @Test
    @PerfTest(invocations = 100, threads = 10)
    @Required(average = 200, max = 500)
    public void testCocheRepositoryFindAllPerformance() {
        cocheRepository.findAll();
    }
}
