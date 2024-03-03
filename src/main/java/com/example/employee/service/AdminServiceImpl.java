package com.example.employee.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.employee.dto.AdminDTO;
import com.example.employee.entities.Admin;
import com.example.employee.exception.AdminNotFoundException;
import com.example.employee.exception.ConstraintViolationException;
import com.example.employee.repository.AdminRepository;

@Service
public class AdminServiceImpl implements AdminService {

	static Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
	private AdminRepository adminRepository;
	private ModelMapper modelMapper;

	@Autowired
	public AdminServiceImpl(AdminRepository adminRepository,ModelMapper modelMapper){
		this.adminRepository = adminRepository;
		this.modelMapper = modelMapper;
	}
	/**
	 * addManager: creating new admin
	 * 
	 * @param AdminDTO adminDto
	 * @return String
	 */
	@Override
	public String addAdmin(AdminDTO adminDto) {
		Admin admin = modelMapper.map(adminDto, Admin.class);
		try {
			Admin savedAdmin = adminRepository.save(admin);
			logger.debug("Admin added" + savedAdmin);
		} catch (Exception e) {
			throw new ConstraintViolationException("could not execute the statement");
		}
		logger.info("admin added");
		return "admin added";
	}

	/**
	 * Fetching all admin details
	 * 
	 * @return List<AdminDTO
	 */
	@Override
	public List<AdminDTO> getAllAdmins() {
		List<AdminDTO> admins = adminRepository.findAll().stream().map(admin -> modelMapper.map(admin, AdminDTO.class))
				.collect(Collectors.toList());
		logger.debug("admins" + admins);
		logger.info("returned the list of admins");
		return admins;
	}

	/**
	 * fetching admin details using adminId
	 * 
	 * @param long adminId
	 * @return AdminDTO
	 * @exception AdminNotFoundException
	 */
	@Override
	public AdminDTO getAdminById(long adminId) {
		Admin admin = adminRepository.findById(adminId)
				.orElseThrow(() -> new AdminNotFoundException("Admin with id: " + adminId + " not found"));
		logger.info("fetching admin details using adminId");
		logger.debug("detched admin details: " + admin);
		return modelMapper.map(admin, AdminDTO.class);
	}

	/**
	 * Updating admin details by adminId
	 * 
	 * @param long     adminId
	 * @param AdminDTO adminDto
	 * @exception AdminNotFoundException
	 */
	@Override
	public String updateAdminDetails(long adminId, AdminDTO adminDto) {
		Admin admin = adminRepository.findById(adminId)
				.orElseThrow(() -> new AdminNotFoundException("Admin with id: " + adminId + " not found"));
		admin.setAdminDepartment(adminDto.getAdminDepartment());
		admin.setAdminName(adminDto.getAdminName());
		try {
			Admin updatedAdmin = adminRepository.save(admin);
			logger.debug("updated admin details: " + updatedAdmin);
			logger.info("Updated admin details by adminId");
		} catch (Exception e) {
			throw new ConstraintViolationException("admin name must not be null");
		}
		return "admin details updated";
	}

	/**
	 * Deleting admin details by adminId
	 * 
	 * @param long adminId
	 * @return String
	 * @exception AdminNotFoundException
	 *
	 */
	@Override
	public String deleteAdminById(long adminId) {
		adminRepository.findById(adminId)
				.orElseThrow(() -> new AdminNotFoundException("No admin found with id: " + adminId + " to delete"));
		adminRepository.deleteById(adminId);
		logger.info("Admin details deleted");
		return "Admin details deleted";
	}

}
