import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransportationService {

    @Autowired
    private TransportationRepository transportationRepository;

    @Transactional
    public boolean checkIfTransportationExists(Long id) {
        Transportation transportation = transportationRepository.findById(id).orElse(null);
        return transportation != null;
    }
}

