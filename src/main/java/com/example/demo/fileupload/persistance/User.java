package com.example.demo.fileupload.persistance;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String uname;

	@Column(nullable = false)
	private String password;
	String accessToken;
	String refreshToken;
}
