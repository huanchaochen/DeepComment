package chc.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import chc.bean.EndLineVerify;

public interface EndLineVerifyRepository extends MongoRepository<EndLineVerify,ObjectId>{
	
}
