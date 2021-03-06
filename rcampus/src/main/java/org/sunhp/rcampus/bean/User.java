package org.sunhp.rcampus.bean;

public class User {
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to
	 * the database column tb_user.user_id
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	private Long userId;

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to
	 * the database column tb_user.user_name
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	private String userName;

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to
	 * the database column tb_user.email
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	private String email;

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to
	 * the database column tb_user.passwd
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	private String passwd;

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to
	 * the database column tb_user.photo
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	private String photo;

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to
	 * the database column tb_user.active_course
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	private Long activeCourse;
	private Long userType;

	/**
	 * This method was generated by MyBatis Generator. This method returns the
	 * value of the database column tb_user.user_id
	 * 
	 * @return the value of tb_user.user_id
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the
	 * value of the database column tb_user.user_id
	 * 
	 * @param userId
	 *            the value for tb_user.user_id
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the
	 * value of the database column tb_user.user_name
	 * 
	 * @return the value of tb_user.user_name
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the
	 * value of the database column tb_user.user_name
	 * 
	 * @param userName
	 *            the value for tb_user.user_name
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the
	 * value of the database column tb_user.email
	 * 
	 * @return the value of tb_user.email
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the
	 * value of the database column tb_user.email
	 * 
	 * @param email
	 *            the value for tb_user.email
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the
	 * value of the database column tb_user.passwd
	 * 
	 * @return the value of tb_user.passwd
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	public String getPasswd() {
		return passwd;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the
	 * value of the database column tb_user.passwd
	 * 
	 * @param passwd
	 *            the value for tb_user.passwd
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the
	 * value of the database column tb_user.photo
	 * 
	 * @return the value of tb_user.photo
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	public String getPhoto() {
		return photo;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the
	 * value of the database column tb_user.photo
	 * 
	 * @param photo
	 *            the value for tb_user.photo
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the
	 * value of the database column tb_user.active_course
	 * 
	 * @return the value of tb_user.active_course
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	public Long getActiveCourse() {
		return activeCourse;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the
	 * value of the database column tb_user.active_course
	 * 
	 * @param activeCourse
	 *            the value for tb_user.active_course
	 * 
	 * @mbggenerated Fri Apr 28 16:03:04 CST 2017
	 */
	public void setActiveCourse(Long activeCourse) {
		this.activeCourse = activeCourse;
	}

	public Long getUserType() {
		return userType;
	}

	public void setUserType(Long userType) {
		this.userType = userType;
	}

}