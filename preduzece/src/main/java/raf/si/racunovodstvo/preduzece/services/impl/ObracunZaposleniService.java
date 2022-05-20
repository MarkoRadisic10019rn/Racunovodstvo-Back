package raf.si.racunovodstvo.preduzece.services.impl;

import org.springframework.stereotype.Service;
import raf.si.racunovodstvo.preduzece.model.Obracun;
import raf.si.racunovodstvo.preduzece.model.ObracunZaposleni;
import raf.si.racunovodstvo.preduzece.model.Plata;
import raf.si.racunovodstvo.preduzece.model.Zaposleni;
import raf.si.racunovodstvo.preduzece.repositories.ObracunRepository;
import raf.si.racunovodstvo.preduzece.repositories.ObracunZaposleniRepository;
import raf.si.racunovodstvo.preduzece.services.IObracunZaposleniService;


import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class ObracunZaposleniService implements IObracunZaposleniService {

    private final ObracunZaposleniRepository obracunZaposleniRepository;

    //TODO zameni obracun repository sa servisom
    private final ObracunRepository obracunRepository;

    private final ZaposleniService zaposleniService;
    private final PlataService plataService;

    public ObracunZaposleniService(ObracunZaposleniRepository obracunZaposleniRepository, ObracunRepository obracunRepository, ZaposleniService zaposleniService, PlataService plataService) {
        this.obracunZaposleniRepository = obracunZaposleniRepository;
        this.obracunRepository = obracunRepository;
        this.zaposleniService = zaposleniService;
        this.plataService = plataService;
    }

    @Override
    public <S extends ObracunZaposleni> S save(S var1) {
        return obracunZaposleniRepository.save(var1);
    }

    @Override
    public Optional<ObracunZaposleni> findById(Long var1) {
        return obracunZaposleniRepository.findById(var1);
    }

    @Override
    public List<ObracunZaposleni> findAll() {
        return obracunZaposleniRepository.findAll();
    }

    @Override
    public void deleteById(Long obracunZaposleniId) {
        Optional<ObracunZaposleni> optionalObracunZaposleni = obracunZaposleniRepository.findById(obracunZaposleniId);
        if (optionalObracunZaposleni.isEmpty()) {
            throw new EntityNotFoundException();
        }
        obracunZaposleniRepository.deleteById(obracunZaposleniId);
    }

    @Override
    public ObracunZaposleni save(Long zaposleniId, Double ucinak, Long obracunId) {

        Optional<Zaposleni> zaposleni = zaposleniService.findById(zaposleniId);
        Optional<Obracun> obracun = obracunRepository.findById(obracunId);
        ObracunZaposleni obracunZaposleni = new ObracunZaposleni();

        if(zaposleni.isPresent()) {
            obracunZaposleni.setZaposleni(zaposleni.get());
        }else{
            throw new EntityNotFoundException();
        }

        if(obracun.isPresent()){
            obracunZaposleni.setObracun(obracun.get());
        }else{
            throw new EntityNotFoundException();
        }

        if(obracunZaposleniRepository.findByZaposleniAndObracun(zaposleni.get(), obracun.get()) != null) {
            throw new EntityExistsException();
        }

        Plata plata = plataService.findPlatabyDatumAndZaposleni(obracun.get().getDatumObracuna(), zaposleni.get());

        if(plata == null){
            throw new EntityNotFoundException();
        }

        obracunZaposleni.setUcinak(ucinak);
        izracunajUcinak(obracunZaposleni, plata);

        return obracunZaposleniRepository.save(obracunZaposleni);
    }

    private void izracunajUcinak(ObracunZaposleni obracunZaposleni, Plata plata){

        Double ucinak = obracunZaposleni.getUcinak();
        obracunZaposleni.setBrutoPlata(plata.getBrutoPlata() == null ? null : plata.getBrutoPlata()*ucinak);
        obracunZaposleni.setDoprinos1(plata.getDoprinos1() == null ? null : plata.getDoprinos1()*ucinak);
        obracunZaposleni.setDoprinos1(plata.getDoprinos2() == null ? null : plata.getDoprinos2()*ucinak);
        obracunZaposleni.setUkupanTrosakZarade(plata.getUkupanTrosakZarade() == null ? null : plata.getUkupanTrosakZarade()*ucinak);
        obracunZaposleni.setNetoPlata(plata.getNetoPlata()*ucinak);
        obracunZaposleni.setKomentar(plata.getKomentar());
        obracunZaposleni.setPorez(plata.getPorez());
    }
}
