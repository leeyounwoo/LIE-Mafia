import UserCam from "../components/UserCam/index";
import { Container, Row, Col } from "react-bootstrap";
import styles from "./RoomGrid.module.css";
function RoomGrid() {
  
  return (
    <Container fluid>
      <Row>
        <Col>
          <UserCam />
        </Col>
        <Col>
          <UserCam />
        </Col>
        <Col>
          <UserCam />
        </Col>
      </Row>
      <Row>
        <Col></Col>
        <Col>
          <h1>공지사항 들어오는 공간</h1>
          <div className={styles.infobox}>
            <div className={styles.infotextwrap}>
              <span className={styles.infotext}>메세지</span>
            </div>
            <div className={styles.policetextwrap}>
              <span className={styles.policetext}>서브메세지</span>
            </div>
          </div>
        </Col>
        <Col></Col>
      </Row>
      <Row>
        <Col>
          <UserCam />
        </Col>
        <Col>
          <UserCam />
        </Col>
        <Col>
          <UserCam />
        </Col>
      </Row>
    </Container>
  );
}
export default RoomGrid;
