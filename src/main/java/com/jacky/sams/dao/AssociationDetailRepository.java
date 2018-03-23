package com.jacky.sams.dao;

import com.jacky.sams.entity.AssociationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AssociationDetailRepository extends JpaRepository<AssociationDetail, String>,JpaSpecificationExecutor<AssociationDetail> {


}
