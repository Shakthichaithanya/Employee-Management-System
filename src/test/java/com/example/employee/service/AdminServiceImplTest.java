package com.example.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.example.employee.dto.AdminDTO;
import com.example.employee.entities.Admin;
import com.example.employee.exception.AdminNotFoundException;
import com.example.employee.exception.ConstraintViolationException;
import com.example.employee.repository.AdminRepository;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

	@Mock
	private AdminRepository adminRepository;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private AdminServiceImpl adminService;

	private AdminDTO adminDTO;

	private Admin admin;

	@BeforeEach
	public void setup() {
		adminDTO = new AdminDTO();
		adminDTO.setAdminId(1);
		adminDTO.setAdminDepartment("account");
		adminDTO.setAdminName("siva");
		adminDTO.setEmail("siva@gmail.com");
		admin = new Admin();
		admin.setAdminId(1);
		admin.setAdminDepartment("account");
		admin.setAdminName("siva");
		admin.setEmail("siva@gmail.com");

	}

	@DisplayName("JUnit test for addAdmin method")
	@Test
	void testAddAdmin() {

		BDDMockito.given(modelMapper.map(adminDTO, Admin.class)).willReturn(admin);
		BDDMockito.given(adminRepository.save(admin)).willReturn(admin);
		String message = adminService.addAdmin(adminDTO);
		assertThat(message).isEqualTo("admin added");
	}

	@DisplayName("Junit test for add admin with constraint violation exception")
	@Test
	void testAddAdminException() {

		BDDMockito.given(adminRepository.save(admin)).willThrow(ConstraintViolationException.class);
		assertThrows(ConstraintViolationException.class, () -> {
			adminService.addAdmin(adminDTO);
		});
	}

	@DisplayName("JUnit test for getting all admins")
	@Test
	void testGetAllAdmins() {
		List<AdminDTO> adminDto = new ArrayList<>();
		adminDto.add(adminDTO);
		List<Admin> admins = new ArrayList<>();
		admins.add(admin);
		BDDMockito.given(adminRepository.findAll()).willReturn(admins);
		List<AdminDTO> result = adminService.getAllAdmins();
		assertThat(result).hasSize(1);
	}

	@DisplayName("Junit test for getting admin by id")
	@Test
	void testGetAdminById() {
		BDDMockito.given(adminRepository.findById(admin.getAdminId())).willReturn(Optional.of(admin));
		BDDMockito.given(modelMapper.map(admin, AdminDTO.class)).willReturn(adminDTO);
		AdminDTO adminDto = adminService.getAdminById(admin.getAdminId());

		assertThat(adminDto).isNotNull();
	}

	@DisplayName("JUnit test for getting admin by id with AdminNotFoundException")
	@Test
	void testGetAdminByIdWithException() {
		BDDMockito.given(adminRepository.findById(admin.getAdminId())).willReturn(Optional.empty());
		Throwable exception = assertThrows(AdminNotFoundException.class,
				() -> adminService.getAdminById(admin.getAdminId()));

		assertThat(exception.getMessage()).isEqualTo("Admin with id: " + admin.getAdminId() + " not found");
	}

	@DisplayName("JUnit test for updating admin by id")
	@Test
	void testUpdateAdminDetails() {
		BDDMockito.given(adminRepository.findById(admin.getAdminId())).willReturn(Optional.of(admin));

		String message = adminService.updateAdminDetails(admin.getAdminId(), adminDTO);

		assertThat(message).isEqualTo("admin details updated");
	}

	@DisplayName("JUnit test for updating admin with AdminNotFoundException")
	@Test
	void testUpdateAdminDetailsWithException() {
		BDDMockito.given(adminRepository.findById(admin.getAdminId())).willReturn(Optional.empty());
		Throwable exception = assertThrows(AdminNotFoundException.class,
				() -> adminService.updateAdminDetails(admin.getAdminId(), adminDTO));
		assertThat(exception.getMessage()).isEqualTo("Admin with id: " + admin.getAdminId() + " not found");
	}

	@DisplayName("JUnit test for updating admin with ConstraintViolationException")
	@Test
	void testUpdateAdminDetailsWithConstraintViolationException() {
		BDDMockito.given(adminRepository.findById(admin.getAdminId())).willReturn(Optional.of(admin));
		BDDMockito.given(adminRepository.save(admin)).willThrow(ConstraintViolationException.class);
		Throwable exception = assertThrows(ConstraintViolationException.class,
				() -> adminService.updateAdminDetails(admin.getAdminId(), adminDTO));
		assertThat(exception.getMessage()).isEqualTo("admin name must not be null");
	}

	@DisplayName("JUnit test for delete admin by id")
	@Test
	void testDeleteAdminById() {
		BDDMockito.given(adminRepository.findById(admin.getAdminId())).willReturn(Optional.of(admin));
		String message = adminService.deleteAdminById(admin.getAdminId());

		assertThat(message).isEqualTo("Admin details deleted");

	}

	@DisplayName("JUnit test for delete admin by id with Exception")
	@Test
	void testDeleteAdminByIdWithException() {
		BDDMockito.given(adminRepository.findById(admin.getAdminId())).willReturn(Optional.empty());

		Throwable exception = assertThrows(AdminNotFoundException.class,
				() -> adminService.deleteAdminById(admin.getAdminId()));

		assertThat(exception.getMessage()).isEqualTo("No admin found with id: " + admin.getAdminId() + " to delete");
	}

}
