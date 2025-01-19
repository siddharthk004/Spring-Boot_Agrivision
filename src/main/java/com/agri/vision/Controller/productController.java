package com.agri.vision.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agri.vision.Model.biofertilizer;
import com.agri.vision.Model.booster;
import com.agri.vision.Model.fertilizer;
import com.agri.vision.Model.fungicide;
import com.agri.vision.Model.herbicide;
import com.agri.vision.Model.insecticide;
import com.agri.vision.Model.nutrient;
import com.agri.vision.Model.organic;
import com.agri.vision.Model.pesticide;
import com.agri.vision.Repo.biofertRepo;
import com.agri.vision.Repo.boostRepo;
import com.agri.vision.Repo.fertRepo;
import com.agri.vision.Repo.fungiRepo;
import com.agri.vision.Repo.herbRepo;
import com.agri.vision.Repo.insectRepo;
import com.agri.vision.Repo.nutriRepo;
import com.agri.vision.Repo.organRepo;
import com.agri.vision.Repo.pestRepo;
import com.agri.vision.helper.messageHelper;

@Controller
@RestController
@CrossOrigin(origins = "/**") // this url is react only this will be accept here
@RequestMapping("/api/v1/auth") // base url http://localhost:8080/ onwards
public class productController {

    // autowiring the pesticide repository
    @Autowired
    private pestRepo pestrepo;

    @Autowired
    private biofertRepo biofertrepo;

    @Autowired
    private boostRepo boostrepo;

    @Autowired
    private fungiRepo fungirepo;

    @Autowired
    private herbRepo herbrepo;

    @Autowired
    private insectRepo insectrepo;

    @Autowired
    private nutriRepo nutrirepo;

    @Autowired
    private organRepo organrepo;

    // autowiring the fertilizer repository
    @Autowired
    private fertRepo fertrepo;


    
    // add or save the new pesticide details here
    @PostMapping("/user/SavePesticide")
    pesticide newProduct(@RequestBody pesticide newProduct) {
        return pestrepo.save(newProduct);
    }

    // get all value from database from /user/ViewAllPesticides
    @GetMapping("/user/ViewAllPesticides")
    List<pesticide> getAllPesticides() {
        return pestrepo.findAll();
    }

    // Get pesticide details by ID
    @GetMapping("/user/ViewPesticide/{id}")
    public ResponseEntity<?> getPesticideById(@PathVariable Integer id) {
        try {
            // Fetch the pesticide by ID
            pesticide pesticide = pestrepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pesticide not found with ID: " + id));

            // Return the pesticide details
            return ResponseEntity.ok(pesticide);
        } catch (Exception e) {
            // Handle exceptions and return a proper error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageHelper(false, "Error: " + e.getMessage()));
        }
    }

    // Delete pesticide details by ID
    @DeleteMapping("/user/DeletePesticide/{id}")
    public ResponseEntity<?> deletePesticideById(@PathVariable Integer id) {
        try {
            // Check if the pesticide exists
            pesticide pesticide = pestrepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pesticide not found with ID: " + id));

            // Delete the pesticide
            pestrepo.delete(pesticide);

            // Return success response
            return ResponseEntity.ok(new messageHelper(true, "Pesticide deleted successfully with ID: " + id));
        } catch (Exception e) {
            // Handle exceptions and return a proper error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageHelper(false, "Error: " + e.getMessage()));
        }
    }

    // add or save the new Fertilizer details here
    @PostMapping("/user/SaveFertilizer")
    fertilizer newProduct(@RequestBody fertilizer newProduct) {
        return fertrepo.save(newProduct);
    }

    // get all value from database from /user/ViewAllFertilizer
    @GetMapping("/user/ViewAllFertilizer")
    List<fertilizer> getAllFertilizers() {
        return fertrepo.findAll();
    }

    // Get Fertilizer details by ID
    @GetMapping("/user/ViewFertilizer/{id}")
    public ResponseEntity<?> getFertilizerById(@PathVariable Integer id) {
        try {
            // Fetch the pesticide by ID
            fertilizer fertilizer = fertrepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Fertilizer not found with ID: " + id));

            // Return the pesticide details
            return ResponseEntity.ok(fertilizer);
        } catch (Exception e) {
            // Handle exceptions and return a proper error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageHelper(false, "Error: " + e.getMessage()));
        }
    }

    // Delete fertilizer details by ID
    @DeleteMapping("/user/DeleteFertilizer/{id}")
    public ResponseEntity<?> deleteFertilizerById(@PathVariable Integer id) {
        try {
            // Check if the pesticide exists
            fertilizer fertilizer = fertrepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Fertilizer not found with ID: " + id));

            // Delete the pesticide
            fertrepo.delete(fertilizer);

            // Return success response
            return ResponseEntity.ok(new messageHelper(true, "Fertilizer deleted successfully with ID: " + id));
        } catch (Exception e) {
            // Handle exceptions and return a proper error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageHelper(false, "Error: " + e.getMessage()));
        }
    }

    // get all value from database from /user/ViewAllOrganicFarm
    @GetMapping("/user/ViewAllOrganicFarm")
    List<organic> getAllOraganicFarm() {
        return organrepo.findAll();
    }
    // Get organicFarm details by ID
    @GetMapping("/user/ViewOrganicFarm/{id}")
    public ResponseEntity<?> getOrganicFarmById(@PathVariable Integer id) {
        try {
            // Fetch the organic farm by ID
            organic organic = organrepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Organic Farm not found with ID: " + id));

            // Return the organic farm details
            return ResponseEntity.ok(organic);
        } catch (Exception e) {
            // Handle exceptions and return a proper error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageHelper(false, "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/user/ViewAllHerbicide")
    List<herbicide> getAllHerbicide() {
        return herbrepo.findAll();
    }

    @GetMapping("/user/ViewHerbicide/{id}")
    public ResponseEntity<?> getHerbicideById(@PathVariable Integer id) {
        try {
            // Fetch the pesticide by ID
            herbicide herbicide = herbrepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("herbicide not found with ID: " + id));

            // Return the pesticide details
            return ResponseEntity.ok(herbicide);
        } catch (Exception e) {
            // Handle exceptions and return a proper error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageHelper(false, "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/user/ViewAllFungicide")
    List<fungicide> getAllFungicide() {
        return fungirepo.findAll();
    }

    // Get Fertilizer details by ID
    @GetMapping("/user/ViewFungicide/{id}")
    public ResponseEntity<?> getFungicideById(@PathVariable Integer id) {
        try {
            // Fetch the pesticide by ID
            fungicide fungicide = fungirepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("fungicide not found with ID: " + id));

            // Return the pesticide details
            return ResponseEntity.ok(fungicide);
        } catch (Exception e) {
            // Handle exceptions and return a proper error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageHelper(false, "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/user/ViewAllBooster")
    List<booster> getAllBooster() {
        return boostrepo.findAll();
    }

    // Get Fertilizer details by ID
    @GetMapping("/user/ViewBooster/{id}")
    public ResponseEntity<?> getBossterById(@PathVariable Integer id) {
        try {
            // Fetch the pesticide by ID
            booster booster = boostrepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("booster not found with ID: " + id));

            // Return the pesticide details
            return ResponseEntity.ok(booster);
        } catch (Exception e) {
            // Handle exceptions and return a proper error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageHelper(false, "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/user/ViewAllNutrient")
    List<nutrient> getAllNutrient() {
        return nutrirepo.findAll();
    }

    // Get Fertilizer details by ID
    @GetMapping("/user/ViewNutrient/{id}")
    public ResponseEntity<?> getBioNutrientById(@PathVariable Integer id) {
        try {
            // Fetch the pesticide by ID
            nutrient nutrient = nutrirepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("nutrient not  found with ID: " + id));

            // Return the pesticide details
            return ResponseEntity.ok(nutrient);
        } catch (Exception e) {
            // Handle exceptions and return a proper error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageHelper(false, "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/user/ViewAllInsecticide")
    List<insecticide> getAllInsecticide() {
        return insectrepo.findAll();
    }

    // Get Fertilizer details by ID
    @GetMapping("/user/ViewInsecticide/{id}")
    public ResponseEntity<?> getInsecticideById(@PathVariable Integer id) {
        try {
            // Fetch the pesticide by ID
            insecticide insecticide = insectrepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("insecticide not found with ID: " + id));

            // Return the pesticide details
            return ResponseEntity.ok(insecticide);
        } catch (Exception e) {
            // Handle exceptions and return a proper error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageHelper(false, "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/user/ViewAllBioFertilizer")
    List<biofertilizer> getAllBioFertilizer() {
        return biofertrepo.findAll();
    }

    // Get Fertilizer details by ID
    @GetMapping("/user/ViewBioFertilizer/{id}")
    public ResponseEntity<?> getBioFertilizerById(@PathVariable Integer id) {
        try {
            // Fetch the pesticide by ID
            biofertilizer biofertilizer = biofertrepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bio Fertilizer not found with ID: " + id));

            // Return the pesticide details
            return ResponseEntity.ok(biofertilizer);
        } catch (Exception e) {
            // Handle exceptions and return a proper error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageHelper(false, "Error: " + e.getMessage()));
        }
    }

}
