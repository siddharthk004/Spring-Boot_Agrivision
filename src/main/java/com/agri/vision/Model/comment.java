package com.agri.vision.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long Uid;
    private Long Pid;
    private int star;
    private String commentText;

    @Lob
    @Column(name = "image", columnDefinition = "BLOB")
    private byte[] image;

    
    @Lob
    @Column(name = "video", columnDefinition = "LONGBLOB")
    private byte[] video;

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return Pid;
    }

    public void setPid(Long pid) {
        Pid = pid;
    }

    public Long getUid() {
        return Uid;
    }

    public void setUid(Long uid) {
        Uid = uid;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public byte[] getVideo() {
        return video;
    }

    public void setVideo(byte[] video) {
        this.video = video;
    }
    

}