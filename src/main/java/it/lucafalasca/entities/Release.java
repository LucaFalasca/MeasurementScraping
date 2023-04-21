package it.lucafalasca.entities;

public class Release {
       private String self;
        private String id;
        private String name;
        private String archived;
        private String released;
        private String releaseDate;
        private String projectId;

        public Release(String self, String id, String name, String archived, String released, String releaseDate, String projectId) {
            this.self = self;
            this.id = id;
            this.name = name;
            this.archived = archived;
            this.released = released;
            this.releaseDate = releaseDate;
            this.projectId = projectId;
        }

        public String getSelf() {
            return self;
        }

        public void setSelf(String self) {
            this.self = self;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArchived() {
            return archived;
        }

        public void setArchived(String archived) {
            this.archived = archived;
        }

        public String getReleased() {
            return released;
        }

        public void setReleased(String released) {
            this.released = released;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

    @Override
    public String toString() {
        return "Release{" +
                "self='" + self + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", archived='" + archived + '\'' +
                ", released='" + released + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", projectId='" + projectId + '\'' +
                '}';
    }
}
