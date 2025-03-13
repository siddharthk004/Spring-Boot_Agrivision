package com.agri.vision.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.AddManage;


@Repository
public interface AddManageRepo extends JpaRepository<AddManage, Long> {

}